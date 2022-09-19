package com.hepo.c2c.social.govern.mall.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hepo.c2c.social.govern.mall.domain.Shop;
import com.hepo.c2c.social.govern.mall.domain.ShopType;
import com.hepo.c2c.social.govern.mall.service.IShopService;
import com.hepo.c2c.social.govern.mall.utils.SystemConstants;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * (Shop)表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/shop")
public class ShopController {

    @Autowired
    private IShopService shopService;

    /**
     * 根据店铺id查询店铺
     *
     * @param id 店铺id
     * @return 店铺信息
     */
    @GetMapping("/{id}")
    public ResultObject<Shop> queryShopById(@PathVariable("id") Long id) {
        return shopService.queryShopById(id);
    }

    /**
     * 添加店铺
     *
     * @param shop
     * @return
     */
    @PostMapping("/save")
    public ResultObject<String> saveShop(@RequestBody Shop shop) {
        return shopService.saveShop(shop);
    }

    /**
     * 修改店铺
     *
     * @param shop
     * @return
     */
    @PostMapping("/update")
    public ResultObject<String> updateShop(@RequestBody Shop shop) {
        return shopService.updateShop(shop);
    }

    /**
     * 删除店铺
     *
     * @param shopId 店铺id
     * @return
     */
    @PostMapping("/delete/{id}}")
    public ResultObject<String> deleteShop(@PathVariable("id") Long shopId) {
        return shopService.deleteShop(shopId);
    }

    /**
     * 根据商铺类型分页查询商铺信息
     *
     * @param typeId  商铺类型
     * @param current 页码
     * @return 商铺信息
     */
    @GetMapping("/of/type")
    public ResultObject<ShopType> queryShopByType(@RequestParam("typeId") Integer typeId, @RequestParam(value = "current", defaultValue = "1") Integer current, @RequestParam("x") Double x, @RequestParam("y") Double y) {
        return shopService.queryShopByType(typeId, current, x, y);
    }

    /**
     * 根据商铺名称关键字分页查询商铺信息
     *
     * @param name    商铺名称
     * @param current 页码
     * @return 商铺信息
     */
    @GetMapping("/of/name")
    public ResultObject<Page<Shop>> queryShopByName(@RequestParam("name") String name, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        Page<Shop> shopPage = shopService.query().like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        return ResultObject.success(shopPage);
    }
}
