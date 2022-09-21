package com.hepo.c2c.social.govern.mall.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hepo.c2c.social.govern.mall.dto.RedisData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.CACHE_NULL_TTL;

/**
 * Description: redis缓存击穿工具类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 16:07
 *
 * @author linhaibo
 */
@Component
@Slf4j
public class CacheClient {


    @Resource
    private StringRedisTemplate stringRedisTemplate;


    private final static ExecutorService CACHE_REBUILD_POOL = Executors.newFixedThreadPool(10);


    /**
     * 根据逻辑过期解决缓存击穿问题
     *
     * @param keyPrefix      key前缀
     * @param id             主键id
     * @param classType      缓存对象类型
     * @param dbFallback     回调函数
     * @param expireTime     redis缓存过期时间
     * @param timeUnit       时间单位
     * @param lockKey        分布式锁的key
     * @param lockExpireTime 分布式锁过期时间
     * @return
     */
    public <ID, T> T queryWithLogicalExpire(String keyPrefix, ID id, Class<T> classType, Function<ID, T> dbFallback, Long expireTime, TimeUnit timeUnit, String lockKey, Long lockExpireTime) {
        //1.查询redis
        String redisKey = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isBlank(json)) {
            //1.1.未命中缓存，直接返回空对象
            log.info("queryWithLogicalExpire-未命中缓存，直接返回空对象");
            stringRedisTemplate.opsForValue().set(redisKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        //2.判断缓存是否过期
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        LocalDateTime logicalExpireTime = redisData.getExpireTime();
        T t = JSONUtil.toBean(JSONUtil.toJsonStr((JSONObject) redisData.getData()), classType);
        if (logicalExpireTime.isAfter(LocalDateTime.now())) {
            //2.1.缓存未过期，直接返回店铺信息
            return t;
        }

        //2.2过期，尝试获取互斥锁
        log.info("queryWithLogicalExpire缓存已过期，尝试获取分布式锁，构建缓存");

        boolean isLock = tryLock(lockKey, lockExpireTime);
        //3.判断是否获取锁
        if (isLock) {
            //再次判断缓存是否过期，Double check
            if (logicalExpireTime.isBefore(LocalDateTime.now())) {
                //3.2获取到锁，开启独立线程
                CACHE_REBUILD_POOL.submit(() -> {
                    try {
                        //重建缓存
                        //查询数据库
                        T newT = dbFallback.apply(id);
                        //写入缓存
                        this.setWithLogicalExpire(redisKey, newT, expireTime, timeUnit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        //释放锁
                        unLock(lockKey);
                    }
                });
            }
        }
        //3.1.没获取到锁，直接返回店铺信息
        log.info("queryWithLogicalExpire没获取到锁，直接返回");

        //6.返回店铺信息
        return t;
    }



    /**
     * 根据互斥锁解决缓存穿透
     *
     * @param keyPrefix      key前缀
     * @param id             主键id
     * @param classType      缓存对象类型
     * @param dbFallback     回调函数
     * @param expireTime     redis缓存过期时间
     * @param timeUnit       时间单位
     * @param lockKey        分布式锁的key
     * @param lockExpireTime 分布式锁过期时间
     * @return
     */
    public <T, ID> T queryWithMutex(String keyPrefix, ID id, Class<T> classType, Function<ID, T> dbFallback, Long expireTime, TimeUnit timeUnit, String lockKey, Long lockExpireTime) {
        String redisKey = keyPrefix + id;
        //1.查询缓存，未命中
        String dataJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isBlank(dataJson)) {
            //2.获取分布式锁
            boolean isLock = tryLock(lockKey, lockExpireTime);
            T t = null;
            while (true) {
                if (isLock) {
                    try {
                        //3.查询数据库进行缓存重建
                        t = dbFallback.apply(id);
                        //4.写入缓存
                        if (t == null) {
                            //4.1如果店铺为空，缓存空对象
                            stringRedisTemplate.opsForValue().set(redisKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                        } else {
                            //4.2设置店铺缓存
                            this.set(redisKey, JSONUtil.toJsonStr(t), expireTime, timeUnit);
                        }
                        return t;
                    } catch (Exception e) {
                        log.error("queryWithPassThrough获取分布式锁失败,{}", e);
                        e.printStackTrace();
                    } finally {
                        //5.释放锁
                        unLock(lockKey);
                    }
                } else {
                    try {
                        //如果没获取到锁，休眠一会
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        //6.如果不为空，直接返回结果
        return JSONUtil.toBean(dataJson, classType);
    }


    private boolean tryLock(String key, long expireTime) {
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", expireTime, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(isLock);
    }

    private void unLock(String key) {
        stringRedisTemplate.delete(key);
    }


    /**
     * 设置redis缓存
     *
     * @param cacheKey   redis的key
     * @param dagtaJson  redis的value
     * @param expireTime redis的过期时间
     * @param timeUnit   redis的过期时间单位
     */
    private void set(String cacheKey, String dagtaJson, Long expireTime, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(cacheKey, dagtaJson, expireTime, timeUnit);
    }


    /**
     * 封装redisData对象，并写入redis
     *
     * @param key        redis key
     * @param value      redis value
     * @param expireTime 逻辑过期时间
     * @param timeUnit   时间单位
     */
    private void setWithLogicalExpire(String key, Object value, Long expireTime, TimeUnit timeUnit) {
        //设置逻辑过期时间
        RedisData redisData = new RedisData();
        redisData.setExpireTime(LocalDateTime.now().plusMinutes(timeUnit.toSeconds(expireTime)));
        redisData.setData(value);
        //写入redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

}
