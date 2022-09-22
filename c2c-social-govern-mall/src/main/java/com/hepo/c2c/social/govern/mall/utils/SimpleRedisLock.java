package com.hepo.c2c.social.govern.mall.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Description: redis分布式锁
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-22 10:52
 *
 * @author linhaibo
 */
public class SimpleRedisLock implements ILock {

    private String name;

    private StringRedisTemplate stringRedisTemplate;

    public SimpleRedisLock(String name, StringRedisTemplate stringRedisTemplate) {
        this.name = name;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    private static final String KEY_PREFIX = "lock:";

    private static final String ID_PREFIX = UUID.randomUUID().toString(true) + "-";

    @Override
    public boolean tryLock(long timeout) {
        String key = KEY_PREFIX + name;
        String value = ID_PREFIX + Thread.currentThread().getId();
        //获取锁
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, value, timeout, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(isLock);
    }

    @Override
    public void unLock() {
        String key = KEY_PREFIX + name;
        String lockValue = ID_PREFIX + Thread.currentThread().getId();
        String redisLockValue = stringRedisTemplate.opsForValue().get(key);
        //判断锁标识是否一致
        if (lockValue.equals(redisLockValue)) {
            //释放锁
            stringRedisTemplate.delete(key);
        }
    }
}
