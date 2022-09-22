package com.hepo.c2c.social.govern.mall;

import com.hepo.c2c.social.govern.mall.service.impl.ShopServiceImpl;
import com.hepo.c2c.social.govern.mall.utils.RedisIdWorker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = MallApplication.class)
public class AppTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ShopServiceImpl shopService;

    @Resource
    RedisIdWorker redisIdWorker;

    @Resource
    RedissonClient redissonClient;

    RLock lock;

    @BeforeEach
    public void setup() {
        lock = redissonClient.getLock("voucher");
    }



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

    @Test
    public void method1() throws InterruptedException {
        //尝试获取锁
        boolean isLock = lock.tryLock(1L, TimeUnit.SECONDS);
        if (!isLock) {
            System.out.println("method1获取锁失败");
            return;
        }

        try {
            System.out.println("method1获取锁成功！");
            method2();
            System.out.println("method1执行业务完毕");
        }finally {
            System.out.println("method1开始释放锁！");
            lock.unlock();
        }
    }

    public void method2(){
        //尝试获取锁
        boolean isLock = lock.tryLock();
        if (!isLock) {
            System.out.println("method2获取锁失败");
            return;
        }

        try {
            System.out.println("method2获取锁成功！");
            Thread.sleep(10);
            System.out.println("method2执行业务完毕");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            System.out.println("method2开始释放锁！");
            lock.unlock();
        }
    }
}
