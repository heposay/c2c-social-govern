package com.hepo.c2c.social.govern.mall.listener;

import cn.hutool.json.JSONUtil;
import com.hepo.c2c.social.govern.mall.domain.VoucherOrder;
import com.hepo.c2c.social.govern.mall.service.ISeckillVoucherService;
import com.hepo.c2c.social.govern.mall.service.IVoucherOrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import static com.hepo.c2c.social.govern.mall.utils.OrderStatusConstants.UNPAY;
import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.*;

/**
 * Description: 创建秒杀订单消息处理监听器
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-23 10:57
 *
 * @author linhaibo
 */

@Component("createVoucherOrderTransactionListener")
@Slf4j
public class CreateVoucherOrderTransactionListener implements TransactionListener {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IVoucherOrderService voucherOrderService;

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        Long orderId = null;
        try {

            String body = new String(msg.getBody(), Charset.defaultCharset());
            log.info("收到创建订单消息：{}", body);
            //1.解析消息体
            VoucherOrder voucherOrder = JSONUtil.toBean(body, VoucherOrder.class);
            orderId = voucherOrder.getId();
            //2.用redis保证幂等性
            Boolean isCreate = stringRedisTemplate.opsForValue().setIfAbsent(CREATE_ORDER_KEY + orderId, "1", CREATE_ORDER_TTL, TimeUnit.MINUTES);
            if (Boolean.FALSE.equals(isCreate)) {
                //已创建过订单了，直接返回
                log.warn("不能重复创建订单，订单号:{}", orderId);
                return LocalTransactionState.COMMIT_MESSAGE;
            }
            //3.创建订单
            createVoucherOrder(voucherOrder);
            log.info("已创建订单，订单号: {}, 消息已提交", orderId);

        } catch (Exception e) {
            //消费失败，删除redis中幂等性的key
            stringRedisTemplate.delete(CREATE_ORDER_KEY + orderId);
            log.error("消费创建订单消息失败:{}", JSONUtil.toJsonStr(msg));
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        //4.手动ack消息
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    /**
     * 创建订单
     *
     * @param voucherOrder
     */
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long voucherId = voucherOrder.getVoucherId();
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
        voucherOrderService.save(voucherOrder);
        log.info("保存订单到数据库成功！订单id为：{}", voucherOrder.getId());
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        System.out.println("check: " + new String(msg.getBody()));
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
