package com.hepo.c2c.social.govern.mall;

import cn.hutool.core.util.PhoneUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest(classes = MallApplication.class)
public class AppTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisTest() {
        System.out.println(stringRedisTemplate.opsForValue().get("k1"));
    }


}
