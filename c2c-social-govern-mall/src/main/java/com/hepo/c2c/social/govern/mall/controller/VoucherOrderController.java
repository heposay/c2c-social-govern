package com.hepo.c2c.social.govern.mall.controller;

import com.hepo.c2c.social.govern.mall.service.IVoucherOrderService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
* (VoucherOrder)表控制层
*
* @author linhaibo
* 
*/
@RestController
@RequestMapping("/voucher-order")
public class VoucherOrderController {

    @Resource
    private IVoucherOrderService voucherOrderService;

    @PostMapping("/seckill/{id}")
    public ResultObject<String> seckill(@PathVariable("id") Long voucherId) {
        return voucherOrderService.seckillVoucher(voucherId);
    }
}
