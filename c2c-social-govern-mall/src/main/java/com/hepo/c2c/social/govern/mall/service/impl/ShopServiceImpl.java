package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.Shop;
import com.hepo.c2c.social.govern.mall.domain.ShopType;
import com.hepo.c2c.social.govern.mall.dto.RedisData;
import com.hepo.c2c.social.govern.mall.mapper.ShopMapper;
import com.hepo.c2c.social.govern.mall.service.IShopService;
import com.hepo.c2c.social.govern.mall.utils.CacheClient;
import com.hepo.c2c.social.govern.vo.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.*;

/**
 * 店铺管理实现类
 *
 * @author linhaibo
 */
@Service
@Slf4j
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存重建的线程池
     */
    private static final ExecutorService CACHE_REBUILD_POOL = Executors.newFixedThreadPool(10);

    @Resource
    private CacheClient cacheClient;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultObject<String> saveShop(Shop shop) {
        //保存数据库
        save(shop);
        //设置缓存
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + shop.getId() + ":" + shop.getName(),
                JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.DAYS);
        return ResultObject.success("添加店铺成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultObject<String> updateShop(Shop shop) {
        //1.校验id
        Long shopId = shop.getId();
        if (shopId == null) {
            return ResultObject.error("店铺id不能为空！");
        }
        //2.修改数据库
        updateById(shop);

        //3.删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shopId);

        return ResultObject.success("修改店铺成功");
    }


    @Override
    public ResultObject<Shop> queryShopById(Long shopId) {
        //缓存击穿方案
        //Shop shop = queryWithPassThrough(shopId);

        //基于互斥锁解决缓存击穿问题
        //Shop shop = queryWithMutex(shopId);

        //基于逻辑过期解决缓存击穿问题
        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY, shopId, Shop.class,
                this::getById, CACHE_SHOP_TTL, TimeUnit.HOURS,
                LOCK_SHOP_KEY + shopId, 10L);
       // Shop shop = queryWithLogicalExpire(shopId);
        //返回结果
        return ResultObject.success(shop);
    }

    /**
     * 基于逻辑过期解决缓存击穿问题
     *
     * @param shopId 店铺id
     * @return 店铺
     */
    private Shop queryWithLogicalExpire(Long shopId) {
        //1.根据shopId查询redis
        String redisKey = CACHE_SHOP_KEY + shopId;
        String shopJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isBlank(shopJson)) {
            //1.1.未命中缓存，直接返回空对象
            log.info("queryWithLogicalExpire-未命中缓存，直接返回空对象");
            stringRedisTemplate.opsForValue().set(redisKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return new Shop();
        }

        //2.判断缓存是否过期
        RedisData shopData = JSONUtil.toBean(shopJson, RedisData.class);
        LocalDateTime expireTime = shopData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now())) {
            //2.1.缓存未过期，直接返回店铺信息
            return JSONUtil.toBean(JSONUtil.toJsonStr((JSONObject)shopData.getData()), Shop.class);
        }

        //2.2过期，尝试获取互斥锁
        log.info("queryWithLogicalExpire缓存已过期，尝试获取分布式锁，构建缓存");

        boolean isLock = tryLock(LOCK_SHOP_KEY + shopId, 10L);
        //3.判断是否获取锁
        if (isLock) {
            //再次判断缓存是否过期，Double check
            if (expireTime.isAfter(LocalDateTime.now())) {
                //2.1.缓存未过期，直接返回店铺信息
                return JSONUtil.toBean(JSONUtil.toJsonStr((JSONObject)shopData.getData()), Shop.class);
            }
            //3.2获取到锁，开启独立线程
            CACHE_REBUILD_POOL.submit(() -> {
                try {
                    //重建缓存
                    this.saveShop2Redis(shopId, 10L);

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    //释放锁
                    unLock(LOCK_SHOP_KEY + shopId);
                }
            });

        }
        //3.1.没获取到锁，直接返回店铺信息
        log.info("queryWithLogicalExpire没获取到锁，直接返回");

        //6.返回店铺信息
        return JSONUtil.toBean(JSONUtil.toJsonStr(shopData.getData()), Shop.class);
    }



    /**
     * 封装redisdata数据，然后保存到redis
     * @param shopId
     * @param expireSecond
     */
    public void saveShop2Redis(Long shopId, long expireSecond) {
        Shop shop = getById(shopId);
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSecond));
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + shopId, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 基于互斥锁解决缓存击穿问题
     *
     * @param shopId 店铺id
     * @return 店铺
     */
    private Shop queryWithMutex(Long shopId) {
        //1.查询缓存，未命中
        String cacheKey = CACHE_SHOP_KEY + shopId;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(shopJson)) {
            //2.获取分布式锁
            Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(LOCK_SHOP_KEY + shopId, "1", 10, TimeUnit.SECONDS);
            Shop shop = null;
            while (true) {
                if (BooleanUtil.isTrue(isLock)) {
                    try {
                        //3.查询数据库进行缓存重建
                        shop = getById(shopId);
                        //4.写入缓存
                        if (shop == null) {
                            //4.1如果店铺为空，缓存空对象
                            stringRedisTemplate.opsForValue().set(cacheKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                        } else {
                            //4.2设置店铺缓存
                            stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.DAYS);
                        }
                        return shop;
                    } catch (Exception e) {
                        log.error("queryWithPassThrough获取分布式锁失败,{}", e);
                        e.printStackTrace();
                    } finally {
                        //5.释放锁
                        stringRedisTemplate.delete(LOCK_SHOP_KEY + shopId);
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
        return JSONUtil.toBean(shopJson, Shop.class);
    }


    /**
     * 缓存击穿解决方案
     *
     * @param shopId
     * @return
     */
    private Shop queryWithPassThrough(Long shopId) {
        String cacheKey = CACHE_SHOP_KEY + shopId;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        Shop shop = JSONUtil.toBean(shopJson, Shop.class);
        //命中缓存
        if (StrUtil.isNotBlank(shopJson)) {
            return shop;
        }
        //未命中缓存，查询数据库
        shop = getById(shopId);

        //4.写入缓存
        if (shop == null) {
            //4.1如果店铺为空，缓存空对象
            stringRedisTemplate.opsForValue().set(cacheKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
        } else {
            //4.2设置店铺缓存
            stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.DAYS);
        }
        //6.如果不为空，直接返回结果
        return shop;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultObject<String> deleteShop(Long shopId) {
        if (shopId == null) {
            return ResultObject.error("店铺id不能为空");
        }
        removeById(shopId);
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shopId);
        return ResultObject.success("删除店铺成功");
    }

    @Override
    public ResultObject<ShopType> queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        return null;
    }



    private boolean tryLock(String key, long expireTime) {
        Boolean isLock = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", expireTime, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(isLock);
    }

    private void unLock(String key) {
        stringRedisTemplate.delete(key);
    }
}