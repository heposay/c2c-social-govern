package com.hepo.c2c.social.govern.reward.api.service;

import java.util.List;

/**
 * 奖励服务接口
 * @author linhaibo
 */
public interface RewardService {
    /**
     * 发放奖励
     *
     * @param reviewerIds 评审员id
     */
    void giveReward(List<Long> reviewerIds);


    String testRPC(String name);
}
