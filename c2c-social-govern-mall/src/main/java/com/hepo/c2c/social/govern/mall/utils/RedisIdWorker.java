package com.hepo.c2c.social.govern.mall.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Description:
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 17:18
 *
 * @author linhaibo
 */
@Component
public class RedisIdWorker {


    private final static long BEGIN_TIMESTAMP = 1640995200L;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成id
     * @param keyPrefix 业务主键
     * @return
     */
    public long nextId(String keyPrefix) {
        //生成时间戳
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond - BEGIN_TIMESTAMP;

        //生成序列号
        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        Long count = stringRedisTemplate.opsForValue().increment("incr:" + keyPrefix + ":" + date);

        //拼接并返回
        return timestamp << 32 | count;
    }
}
