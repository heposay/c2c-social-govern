package com.hepo.c2c.social.govern.reward.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.reward.api.service.RewardService;
import com.hepo.c2c.social.govern.reward.domain.RewardCoin;
import com.hepo.c2c.social.govern.reward.mapper.RewardMapper;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;

/**
 * 发放奖励接口实现类
 *
 * @author linhaibo
 */
@Service(version = "1.0.0", interfaceClass = RewardService.class, cluster = "failfast")
public class RewardServiceImpl extends ServiceImpl<RewardMapper, RewardCoin> implements RewardService {


    /**
     * 发放奖励
     *
     * @param reviewerIds 评审员id
     */
    @Override
    public void giveReward(List<Long> reviewerIds) {
        for (Long reviewerId : reviewerIds) {
            RewardCoin rewardCoin = new RewardCoin();
            rewardCoin.setReviewerId(reviewerId);
            rewardCoin.setCoins(10);
            baseMapper.insert(rewardCoin);
        }
    }
}
