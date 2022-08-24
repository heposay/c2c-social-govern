package com.hepo.c2c.social.govern.reward.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.reward.api.service.RewardService;
import com.hepo.c2c.social.govern.reward.domain.RewardCoin;
import com.hepo.c2c.social.govern.reward.mapper.RewardMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
 * 发放奖励接口实现类
 *
 * @author linhaibo
 */
@DubboService(group = "DEFAULT_GROUP", interfaceClass = RewardService.class, cluster = "failfast")
public class RewardServiceImpl extends ServiceImpl<RewardMapper, RewardCoin> implements RewardService {


    /**
     * 发放奖励
     *
     * @param reviewerIds 评审员id
     */
    @Override
    public void giveReward(List<Long> reviewerIds) {

    }

    @Override
    public String testRPC(String name) {
        return "Hello, RPC~~~" + name;
    }
}
