package com.hepo.c2c.social.govern.mall.mq;

import com.alibaba.cloud.stream.binder.rocketmq.constant.RocketMQConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.Map;

/**
 * Description: 生产者工具类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-23 11:02
 *
 * @author linhaibo
 */
@Component
public class MqProducer {

    @Autowired
    private StreamBridge streamBridge;

    /**
     * 发送消息
     *
     * @param payload     消息体
     * @param bindingName 绑定队列名称
     * @param headers     请求头
     * @return
     */
    public boolean sendMsg(String payload, String bindingName, Map<String, Object> headers) {
        MessageBuilder<String> mb = MessageBuilder.withPayload(payload);
        mb.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        mb.setHeader(RocketMQConst.USER_TRANSACTIONAL_ARGS, "binder");
        headers.forEach(mb::setHeader);
        Message<String> msg = mb.build();
        return streamBridge.send(bindingName, msg);
    }
}
