package com.hepo.c2c.social.govern.reviewer.api.service;

import java.util.List;

/**
 * 评审员接口类
 *
 * @author linhaibo
 */
public interface ReviewerService {
    /**
     * 选择评审员
     * @param reportTaskId 举报任务id
     * @return 评审员用户id
     */
    List<Long> selectReviewers(Long reportTaskId);

    /**
     * 完成投票
     * @param reviewerId 评审员id
     * @param reportTaskId 举报任务id
     */
    void finishVote(Long reviewerId, Long reportTaskId);

    String testRPC(String name);
}
