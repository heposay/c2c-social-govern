package com.hepo.c2c.social.govern.reviewer.service;

import com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService;
import org.apache.dubbo.common.constants.LoadbalanceRules;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.reviewer.domain.ReviewerTaskStatus;
import com.hepo.c2c.social.govern.reviewer.mapper.ReviewerMapper;

/**
 * 评审员接口实现类
 *
 * @author linhaibo
 */
@DubboService(interfaceClass = ReviewerService.class, cluster = "failfast", loadbalance = LoadbalanceRules.ROUND_ROBIN)
public class ReviewerServiceImpl extends ServiceImpl<ReviewerMapper, ReviewerTaskStatus> implements ReviewerService {

    /**
     * 选择评审员
     *
     * @param reportTaskId 举报任务id
     * @return 评审员列表
     */
    @Override
    public List<Long> selectReviewers(Long reportTaskId) {
        return null;
    }

    /**
     * 完成投票
     *
     * @param reviewerId   评审员id
     * @param reportTaskId 举报任务id
     */
    @Override
    public void finishVote(Long reviewerId, Long reportTaskId) {

    }

    @Override
    public String testRPC(String name) {
        return "Hello, rpc ~~~~ " + name;
    }
}
