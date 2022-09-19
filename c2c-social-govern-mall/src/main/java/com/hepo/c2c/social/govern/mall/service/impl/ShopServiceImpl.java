package com.hepo.c2c.social.govern.mall.service.impl;

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
        //1.从Redis查询店铺缓存
        String cacheKey = CACHE_SHOP_KEY + shopId;
        String shopJson = stringRedisTemplate.opsForValue().get(cacheKey);
        //2.判断是否存在
        if (StrUtil.isNotBlank(shopJson)) {
            return ResultObject.success(JSONUtil.toBean(shopJson, Shop.class));
        }
        //3.判断是否空值
        if (shopJson != null) {
            return ResultObject.error("店铺信息不存在!");
        }
        //4.不存在，根据id查询数据库
        Shop shop = getById(shopId);

        //5.如果店铺不存在，缓存空对象到redis，防止缓存穿透
        if (shop == null) {
            stringRedisTemplate.opsForValue().set(cacheKey, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return ResultObject.error("店铺不存在！");
        }
        //6.设置redis缓存
        stringRedisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.DAYS);
        //6.返回结果
        return ResultObject.success(shop);
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