package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Stopwatch;
import com.hepo.c2c.social.govern.mall.domain.VoucherOrder;
import com.hepo.c2c.social.govern.mall.mapper.VoucherOrderMapper;
import com.hepo.c2c.social.govern.mall.mq.MqProducer;
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
import java.util.concurrent.*;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.LOCK_ORDER_KEY;

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
    private RedisIdWorker redisIdWorker;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MqProducer mqProducer;

    /**
     * 处理发送消息的线程池
     */
    private static final ExecutorService MQ_SEND_HANDLE_POOL = Executors.newFixedThreadPool(20);

    private static final String BINDING_KEY = "producer-out-0";


    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    @Override
    public ResultObject<String> seckillVoucher(Long voucherId) {
        Stopwatch started = Stopwatch.createStarted();
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
            log.info("库存不足，耗时:{}", started.elapsed(TimeUnit.MILLISECONDS));
            return ResultObject.error(r == 1 ? "库存不足" : "不能重复下单");
        }
        //3.2.为0代表有购买资格

        //创建锁对象
        RLock lock = redissonClient.getLock(LOCK_ORDER_KEY + userId);

        boolean isLock = lock.tryLock();
        int count = 0;
        while (!isLock) {
            //如果加锁失败，睡眠10秒钟
            try {
                if (++count == 3) {
                    log.error("重复获取锁3次失败，记录记录");
                    return ResultObject.error("获取锁失败");
                }
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            isLock = lock.tryLock();
        }
        try {
            long orderId = redisIdWorker.nextId("order");
            //采用线程池异步发送mq消息
            MQ_SEND_HANDLE_POOL.submit(() -> {
                VoucherOrder voucherOrder = new VoucherOrder();
                voucherOrder.setId(orderId);
                voucherOrder.setUserId(Long.valueOf(userId));
                voucherOrder.setVoucherId(voucherId);
                sendCreateOrderMessage(voucherOrder);
            });
            log.info("秒杀成功！订单id为:{}, 耗时:{}", orderId, started.elapsed(TimeUnit.MILLISECONDS));
            return ResultObject.success("秒杀成功！订单id为：" + orderId);
        } finally {
            lock.unlock();
        }
    }

    public void sendCreateOrderMessage(VoucherOrder voucherOrder) {
        int count = 0;
        boolean isSend = mqProducer.sendMsg(JSONUtil.toJsonStr(voucherOrder), BINDING_KEY, Collections.emptyMap());
        log.info("发送创建订单消息，状态:{},订单号为:{}, 用户Id为：{}", isSend, voucherOrder.getId(), voucherOrder.getUserId());
        while (!isSend) {
            if (++count == 3) {
                //TODO 将该消息记录到数据库，后期做人工补偿
                log.error("已重试三次发消息失败，将该消息记录到数据库");
            }
            isSend = mqProducer.sendMsg(JSONUtil.toJsonStr(voucherOrder), BINDING_KEY, Collections.emptyMap());
            log.info("第{}次发送创建订单消息，状态:{},订单号为:{}, 用户Id为：{}", count, isSend, voucherOrder.getId(), voucherOrder.getUserId());
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
