package com.hepo.c2c.social.govern.mall;

import com.hepo.c2c.social.govern.mall.service.impl.ShopServiceImpl;
import com.hepo.c2c.social.govern.mall.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest(classes = MallApplication.class)
public class AppTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopServiceImpl shopService;

    @Resource
    RedisIdWorker redisIdWorker;

    @Test
    public void redisTest() {
        System.out.println(stringRedisTemplate.opsForValue().get("k1"));
    }

    @Test
    public void testSaveRedis() {
        shopService.saveShop2Redis(1L, 10L);

    }


    @Test
    public void testRedisIdWorker() {
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {

            long id = redisIdWorker.nextId("order");
        }
        long end = System.currentTimeMillis();
        System.out.println("生成1000000个id，耗时：" + (end - begin));
    }

}
