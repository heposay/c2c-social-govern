package com.hepo.c2c.social.govern.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.VoucherOrder;
import com.hepo.c2c.social.govern.mall.mapper.VoucherOrderMapper;
import com.hepo.c2c.social.govern.mall.service.ISeckillVoucherService;
import com.hepo.c2c.social.govern.mall.service.IVoucherOrderService;
import com.hepo.c2c.social.govern.mall.utils.RedisIdWorker;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hepo.c2c.social.govern.mall.utils.OrderStatusConstants.UNPAY;

/**
 * Description:  库存订单实现类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
@Service
@Slf4j
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成订单的阻塞队列
     */
    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);

    /**
     * 处理秒杀下单的线程池
     */
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * 处理异步下单内部类
     */
    private class VoucherOrderHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    //1.获取异步队列里面的订单
                    VoucherOrder voucherOrder = orderTasks.take();
                    //2.处理订单
                    createVoucherOrder(voucherOrder);
                } catch (InterruptedException e) {
                    log.error("处理订单异常" + e);
                }
            }
        }
    }

    @Override
    public ResultObject<String> seckillVoucher(Long voucherId) {
        //1.获取用户信息
        String userId = UserHolder.getUser().getId();
        //2.执行lua秒杀脚本
        Long result = stringRedisTemplate.execute(SECKILL_SCRIPT,
                Collections.emptyList(),// KEYS[]
                voucherId.toString(), userId //ARGV[]
        );
        int r = result.intValue();
        //3.判断是否为0
        if (r != 0) {
            //3.1.不为0代表没有购买资格
            return ResultObject.error(r == 1 ? "库存不足" : "不能重复下单");
        }
        //3.2.为0代表有购买资格
        long orderId = redisIdWorker.nextId("order");
        // 4.保存阻塞队列
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setVoucherId(voucherId);
        voucherOrder.setUserId(Long.valueOf(userId));
        orderTasks.add(voucherOrder);

        //5.返回订单id
        return ResultObject.success("秒杀成功！订单id为：" + orderId);
    }

    /**
     * 下单
     *
     * @param voucherOrder 订单信息
     */
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();

        //创建锁对象
        RLock lock = redissonClient.getLock("lock:order:" + userId.toString());

        boolean isLock = lock.tryLock();
        if (!isLock) {
            log.error("获取锁失败！");
            return;
        }
        try {
            //4.查询订单
            Long count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
            if (count > 0) {
                //5.一人一单
                log.error("该用户已经购买过一次！");
                return;
            }

            //6..扣减库存
            boolean isSuccess = seckillVoucherService.update()
                    .setSql("stock = stock - 1")
                    .eq("voucher_id", voucherId)
                    .gt("stock ", 0).update();
            if (!isSuccess) {
                log.error("扣减库存失败！");
                return;
            }
            //7.4支付状态
            voucherOrder.setStatus(UNPAY);
            //8.保存数据库
            save(voucherOrder);
            log.info("秒杀成功！订单id为：{}" , voucherOrder.getId());
        }finally {
            lock.unlock();
        }
    }

    /**
     * 基于分布式锁来实现一人一单功能
     *
     * @param voucherId 优惠券id
     */
//    @Transactional
//    public ResultObject<String> createVoucherOrder(Long voucherId) {
//        UserDTO user = UserHolder.getUser();
//        //创建锁对象
//        RLock lock = redissonClient.getLock("lock:order:" + user.getId());
//
//        boolean isLock = lock.tryLock();
//        if (!isLock) {
//            return ResultObject.error("获取锁失败！");
//        }
//        try {
//            //4.查询订单
//            Long count = query().eq("user_id", user.getId()).eq("voucher_id", voucherId).count();
//            if (count > 0) {
//                //5.一人一单
//                return ResultObject.error("该用户已经购买过一次！");
//            }
//
//            //6..扣减库存
//            boolean isSuccess = seckillVoucherService.update()
//                    .setSql("stock = stock - 1")
//                    .eq("voucher_id", voucherId)
//                    .gt("stock ", 0).update();
//            if (!isSuccess) {
//                return ResultObject.error("扣减库存失败！");
//            }
//            //7.创建订单
//            VoucherOrder voucherOrder = new VoucherOrder();
//            //7.1订单id
//            long orderId = redisIdWorker.nextId("order");
//            voucherOrder.setId(orderId);
//            //7.2优惠券id
//            voucherOrder.setVoucherId(voucherId);
//            //7.3用户id
//            voucherOrder.setUserId(Long.valueOf(user.getId()));
//            //7.4支付状态
//            voucherOrder.setStatus(UNPAY);
//            //8.保存数据库
//            save(voucherOrder);
//            return ResultObject.success("秒杀成功！订单id为：" + orderId);
//        } finally {
//            lock.unlock();
//        }
//    }

    //单机版加锁
//    @Transactional
//    public ResultObject<String> createVoucherOrder(Long voucherId) {
//        UserDTO user = UserHolder.getUser();
//        synchronized (user.getId().intern()) {
//            //4.查询订单
//            Long count = query().eq("user_id", user.getId()).eq("voucher_id", voucherId).count();
//            if (count > 0) {
//                //5.一人一单
//                return ResultObject.error("该用户已经购买过一次！");
//            }
//
//            //6..扣减库存
//            boolean isSuccess = seckillVoucherService.update()
//                    .setSql("stock = stock - 1") //set stock = stock - 1
//                    .eq("voucher_id", voucherId)
//                    .gt("stock ", 0).update();
//            if (!isSuccess) {
//                return ResultObject.error("扣减库存失败！");
//            }
//            //7.创建订单
//            VoucherOrder voucherOrder = new VoucherOrder();
//            //7.1订单id
//            long orderId = redisIdWorker.nextId("order");
//            voucherOrder.setId(orderId);
//            //7.2优惠券id
//            voucherOrder.setVoucherId(voucherId);
//            //7.3用户id
//            voucherOrder.setUserId(Long.valueOf(user.getId()));
//            //7.4支付状态
//            voucherOrder.setStatus(UNPAY);
//            //8.保存数据库
//            save(voucherOrder);
//            return ResultObject.success("秒杀成功！订单id为：" + orderId);
//        }
//    }
}
