package com.hepo.c2c.social.govern.mall;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.BooleanUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Description:
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-10-24 11:38
 *
 * @author linhaibo
 */
@SpringBootTest
@Slf4j
public class RateLimitTest {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    private static final DefaultRedisScript<Long> RATELIMIT_SCRIPT;

    static {
        RATELIMIT_SCRIPT = new DefaultRedisScript<>();
        RATELIMIT_SCRIPT.setLocation(new ClassPathResource("ratelimit.lua"));
        RATELIMIT_SCRIPT.setResultType(Long.class);
    }


    @Test
    public void rateLimitTest() throws InterruptedException {
        String key = "test_rateLimit_key";
        int max = 10;
        int rate = 10;
        AtomicInteger successCount = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch downLatch = new CountDownLatch(30);
        for (int i = 0; i < 30; i++) {
            executorService.execute(() -> {
                boolean isAllow = rateLimit(key, max, rate);
                if (isAllow) {
                    successCount.addAndGet(1);
                }
                log.info("请求放行结果：" + isAllow);
                downLatch.countDown();
            });
        }
        downLatch.await();
        log.info("请求成功{}次", successCount);
    }

    private boolean rateLimit(String key, int max, int rate) {
        List<String> keyList = new ArrayList<>(1);
        keyList.add(key);
        Long result = stringRedisTemplate.execute(RATELIMIT_SCRIPT,
                keyList,
                Integer.toString(max),
                Integer.toString(rate),
                Long.toString(System.currentTimeMillis()));
        assert result != null;
        return result == 1;
    }


}
