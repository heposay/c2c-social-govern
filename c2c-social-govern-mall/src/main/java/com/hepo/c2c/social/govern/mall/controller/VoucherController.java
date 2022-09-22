package com.hepo.c2c.social.govern.mall.controller;

import com.hepo.c2c.social.govern.mall.domain.Voucher;
import com.hepo.c2c.social.govern.mall.service.IVoucherService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;


/**
* (Voucher)表控制层
*
* @author linhaibo
* 
*/
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    /**
     * 新增秒杀券
     * @param voucher 优惠券信息，包含秒杀信息
     * @return 优惠券id
     */
    @PostMapping("/add/seckill")
    public ResultObject<String> addSeckill(@RequestBody Voucher voucher) {
        return voucherService.addSeckill(voucher);
    }

    /**
     * 新增普通券
     * @param voucher 优惠券信息
     * @return 优惠券id
     */
    @PostMapping("/add")
    public ResultObject<String> addVoucher(@RequestBody Voucher voucher) {
        voucher.setCreateTime(LocalDateTime.now());
        voucher.setUpdateTime(LocalDateTime.now());
        boolean isSuccess = voucherService.save(voucher);
        if (!isSuccess) {
            return ResultObject.error("新增优惠券失败！");
        }
        return ResultObject.success("新增优惠券成功！");
    }

    /**
     * 查询店铺的优惠券列表
     * @param shopId 店铺id
     * @return 优惠券列表
     */
    @GetMapping("/list/{shopId}")
    public ResultObject<List<Voucher>> queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
        return voucherService.queryVoucherOfShop(shopId);
    }

}
