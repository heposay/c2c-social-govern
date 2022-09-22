package com.hepo.c2c.social.govern.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.SeckillVoucher;
import com.hepo.c2c.social.govern.mall.domain.VoucherOrder;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.mapper.VoucherOrderMapper;
import com.hepo.c2c.social.govern.mall.service.ISeckillVoucherService;
import com.hepo.c2c.social.govern.mall.service.IVoucherOrderService;
import com.hepo.c2c.social.govern.mall.utils.RedisIdWorker;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

import static com.hepo.c2c.social.govern.mall.utils.OrderStatusConstants.UNPAY;

/**
 * Description:  库存订单实现类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public ResultObject<String> seckillVoucher(Long voucherId) {
        //1.查询优惠券信息
        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);

        //2.判断秒杀是否开始
        LocalDateTime now = LocalDateTime.now();
        if (seckillVoucher.getBeginTime().isAfter(now)) {
            return ResultObject.error("活动尚未开始！");
        }
        if (seckillVoucher.getEndTime().isBefore(now)) {
            return ResultObject.error("活动已经结束！");
        }
        //3.判断库存是否充足
        Integer stock = seckillVoucher.getStock();
        if (stock < 1) {
            //库存不足
            return ResultObject.error("库存不足！");
        }

        return createVoucherOrder(voucherId);
    }

    /**
     * 基于分布式锁来实现一人一单功能
     *
     * @param voucherId 优惠券id
     */
    @Transactional
    public ResultObject<String> createVoucherOrder(Long voucherId) {
        UserDTO user = UserHolder.getUser();
        //创建锁对象
        RLock lock = redissonClient.getLock("lock:order:" + user.getId());

        boolean isLock = lock.tryLock();
        if (!isLock) {
            return ResultObject.error("获取锁失败！");
        }
        try {
            //4.查询订单
            Long count = query().eq("user_id", user.getId()).eq("voucher_id", voucherId).count();
            if (count > 0) {
                //5.一人一单
                return ResultObject.error("该用户已经购买过一次！");
            }

            //6..扣减库存
            boolean isSuccess = seckillVoucherService.update()
                    .setSql("stock = stock - 1")
                    .eq("voucher_id", voucherId)
                    .gt("stock ", 0).update();
            if (!isSuccess) {
                return ResultObject.error("扣减库存失败！");
            }
            //7.创建订单
            VoucherOrder voucherOrder = new VoucherOrder();
            //7.1订单id
            long orderId = redisIdWorker.nextId("order");
            voucherOrder.setId(orderId);
            //7.2优惠券id
            voucherOrder.setVoucherId(voucherId);
            //7.3用户id
            voucherOrder.setUserId(Long.valueOf(user.getId()));
            //7.4支付状态
            voucherOrder.setStatus(UNPAY);
            //8.保存数据库
            save(voucherOrder);
            return ResultObject.success("秒杀成功！订单id为：" + orderId);
        } finally {
            lock.unlock();
        }
    }

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
