package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.Shop;
import com.hepo.c2c.social.govern.mall.domain.ShopType;
import com.hepo.c2c.social.govern.mall.mapper.ShopMapper;
import com.hepo.c2c.social.govern.mall.service.IShopService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.concurrent.TimeUnit;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.*;

/**
 * 店铺管理实现类
 *
 * @author linhaibo
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


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
        Shop shop = queryWithPassThrough(shopId);
        //返回结果
        return ResultObject.success(shop);
    }

    /**
     * 缓存击穿解决方案
     * @param shopId
     * @return
     */
    private Shop queryWithPassThrough(Long shopId) {
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
                        }else {
                            //4.2设置店铺缓存
                            stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.DAYS);
                        }
                        return shop;
                    }catch (Exception e) {
                        log.error("queryWithPassThrough获取分布式锁失败,{}", e);
                        e.printStackTrace();
                    }finally {
                        //5.释放锁
                        stringRedisTemplate.delete(LOCK_SHOP_KEY + shopId);
                    }
                }else {
                    try {
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
}