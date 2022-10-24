package com.hepo.c2c.social.govern.mall.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.hepo.c2c.social.govern.mall.domain.Item;
import com.hepo.c2c.social.govern.mall.domain.ItemStock;
import com.hepo.c2c.social.govern.mall.service.ItemService;
import com.hepo.c2c.social.govern.mall.service.ItemStockService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 商品表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemStockService stockService;

    @Autowired
    private Cache<Long, Item> itemCache;

    @Autowired
    private Cache<Long, ItemStock> stockCache;

    @GetMapping("/{id}")
    public ResultObject<Item> queryById(@PathVariable("id") Long id) {
        return ResultObject.success(itemCache.get(id,
                key -> itemService.query().ne("status", 3).eq("id", key).one())
        );
    }

    @GetMapping("/stock/{id}")
    public ResultObject<ItemStock> queryStockById(@PathVariable("id") Long id) {
        return ResultObject.success(stockCache.get(id, key -> stockService.getById(key)));
    }
}
