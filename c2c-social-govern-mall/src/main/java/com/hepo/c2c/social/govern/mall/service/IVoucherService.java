package com.hepo.c2c.social.govern.mall.service;

import com.hepo.c2c.social.govern.mall.domain.Voucher;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hepo.c2c.social.govern.vo.ResultObject;

import java.util.List;

/**
 * Description: 优惠券接口
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
public interface IVoucherService extends IService<Voucher> {


    ResultObject<String> addSeckill(Voucher voucher);

    ResultObject<List<Voucher>> queryVoucherOfShop(Long shopId);
}
