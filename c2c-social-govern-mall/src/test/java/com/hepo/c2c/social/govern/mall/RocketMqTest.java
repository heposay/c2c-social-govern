package com.hepo.c2c.social.govern.mall;

import cn.hutool.json.JSONUtil;
import com.hepo.c2c.social.govern.mall.domain.VoucherOrder;
import com.hepo.c2c.social.govern.mall.mq.MqProducer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Collections;

/**
 * Description:
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-23 14:06
 *
 * @author linhaibo
 */
@SpringBootTest
public class RocketMqTest {


    @Resource
    private MqProducer mqProducer;

    @Test
    public void producerTest() {
        for (int i = 0; i < 100; i++) {
            VoucherOrder voucherOrder = new VoucherOrder();
            voucherOrder.setUserId((long) i);
            voucherOrder.setVoucherId((long) i);
            voucherOrder.setId((long) i);
            mqProducer.sendMsg(JSONUtil.toJsonStr(voucherOrder), "producer-out-0", Collections.emptyMap());
        }

    }
}
