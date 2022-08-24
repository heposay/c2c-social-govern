package com.hepo.c2c.social.govern.report.service;

import com.hepo.c2c.social.govern.report.domain.ReportTaskVote;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 举报任务投票接口
 *
 * @author linhaibo
 */
public interface ReportTaskVoteService extends IService<ReportTaskVote> {


    /**
     * 始化这批评审员对举报任务的投票状态
     *
     * @param reviewerIds 评审员ID集合
     * @param taskId      举报任务ID
     */
    void initVotes(List<Long> reviewerIds, Long taskId);

    /**
     * 对举报任务进行投票
     *
     * @param reviewerId   评审员ID
     * @param reportTaskId 举报任务ID
     * @param voteResult   投票结果
     */
    void vote(Long reviewerId, Long reportTaskId, Integer voteResult);

    /**
     * 对举报任务进行归票
     *
     * @param reportTaskId
     * @return
     */
    Boolean caculateVotes(Long reportTaskId);

    /**
     * 查询举报任务的投票
     *
     * @param reportTaskId 举报任务ID
     * @return
     */
    List<ReportTaskVote> queryByReportTaskId(Long reportTaskId);

}
