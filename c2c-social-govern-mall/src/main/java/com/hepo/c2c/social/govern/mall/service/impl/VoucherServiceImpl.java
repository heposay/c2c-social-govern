package com.hepo.c2c.social.govern.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.SeckillVoucher;
import com.hepo.c2c.social.govern.mall.domain.Voucher;
import com.hepo.c2c.social.govern.mall.mapper.VoucherMapper;
import com.hepo.c2c.social.govern.mall.service.ISeckillVoucherService;
import com.hepo.c2c.social.govern.mall.service.IVoucherService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.SECKILL_STOCK_KEY;

/**
 * Description: 优惠券实现类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultObject<String> addSeckill(Voucher voucher) {
        //保存数据库
        save(voucher);
        //保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        //保存秒杀库存到redis
        stringRedisTemplate.opsForValue().set(SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString());
        return ResultObject.success("添加秒杀优惠券成功");
    }

    @Override
    public ResultObject<List<Voucher>> queryVoucherOfShop(Long shopId) {
        //查询优惠券
        List<Voucher> list = getBaseMapper().queryVoucherOfShop(shopId);
        if (list.isEmpty()) {
            return ResultObject.success(Collections.emptyList());
        }
        return ResultObject.success(list);
    }
}
