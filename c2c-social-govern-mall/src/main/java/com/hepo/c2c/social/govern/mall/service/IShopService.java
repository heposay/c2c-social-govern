package com.hepo.c2c.social.govern.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hepo.c2c.social.govern.mall.domain.Shop;
import com.hepo.c2c.social.govern.mall.domain.ShopType;
import com.hepo.c2c.social.govern.vo.ResultObject;

import java.util.List;

/**
 * Shop Service层接口
 *
 * @author linhaibo
 */
public interface IShopService extends IService<Shop>{

    ResultObject<String> saveShop(Shop shop);

    ResultObject<String> updateShop(Shop shop);

    ResultObject<Shop> queryShopById(Long id);

    ResultObject<String> deleteShop(Long shopId);

    ResultObject<List<Shop>> queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
