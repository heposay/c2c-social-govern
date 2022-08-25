package com.hepo.c2c.social.govern.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.report.domain.ReportTask;
import com.hepo.c2c.social.govern.report.domain.ReportTaskVote;
import com.hepo.c2c.social.govern.report.mapper.ReportTaskVoteMapper;
import com.hepo.c2c.social.govern.report.service.ReportTaskService;
import com.hepo.c2c.social.govern.report.service.ReportTaskVoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 举报任务投票结果接口实现类
 *
 * @author linhaibo
 */
@Service
public class ReportTaskVoteServiceImpl extends ServiceImpl<ReportTaskVoteMapper, ReportTaskVote> implements ReportTaskVoteService {

    @Autowired
    private ReportTaskService reportTaskService;

    /**
     * 始化这批评审员对举报任务的投票状态
     *
     * @param reviewerIds 评审员ID集合
     * @param taskId      举报任务ID
     */
    @Override
    public void initVotes(List<Long> reviewerIds, Long taskId) {
        for (Long reviewerId : reviewerIds) {
            ReportTaskVote taskVote = new ReportTaskVote();
            taskVote.setReportTaskId(taskId);
            taskVote.setReviewerId(reviewerId);
            taskVote.setVoteResult(ReportTaskVote.UNAPPROVED);
            baseMapper.insert(taskVote);
        }

    }

    @Override
    public void vote(Long reviewerId, Long reportTaskId, Integer voteResult) {
        ReportTaskVote taskVote = new ReportTaskVote();
        taskVote.setReportTaskId(reportTaskId);
        taskVote.setReviewerId(reviewerId);
        taskVote.setVoteResult(voteResult);
        baseMapper.updateById(taskVote);
    }

    /**
     * @param reportTaskId
     * @return
     */
    @Override
    public Boolean caculateVotes(Long reportTaskId) {
        LambdaQueryWrapper<ReportTaskVote> queryWrapper = new LambdaQueryWrapper<ReportTaskVote>()
                .eq(ReportTaskVote::getReportTaskId, reportTaskId);
        List<ReportTaskVote> reportTaskVotes = baseMapper.selectList(queryWrapper);

        int quorum = reportTaskVotes.size() / 2 + 1;

        Integer approveVotes = 0;
        Integer unapproveVotes = 0;

        for (ReportTaskVote reportTaskVote : reportTaskVotes) {
            if (reportTaskVote.getVoteResult().equals(ReportTaskVote.APPROVED)) {
                approveVotes++;
            } else if (reportTaskVote.getVoteResult().equals(ReportTaskVote.UNAPPROVED)) {
                unapproveVotes++;
            }
        }

        ReportTask reportTask = new ReportTask();
        reportTask.setId(reportTaskId);
        if (approveVotes >= quorum) {
            //说明已经完成归票
            reportTask.setVoteResult(ReportTask.VOTE_RESULT_APPROVED);
            reportTaskService.updateById(reportTask);
            return true;
        } else if (unapproveVotes >= quorum) {
            //说明还未完成归票
            reportTask.setVoteResult(ReportTask.VOTE_RESULT_UNAPPROVED);
            reportTaskService.updateById(reportTask);
            return true;
        }

        return false;
    }

    @Override
    public List<ReportTaskVote> queryByReportTaskId(Long reportTaskId) {
        if (reportTaskId == null) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ReportTaskVote> queryWrapper = new LambdaQueryWrapper<ReportTaskVote>()
                .eq(ReportTaskVote::getReportTaskId, reportTaskId);
        List<ReportTaskVote> reportTaskVotes = baseMapper.selectList(queryWrapper);
        return reportTaskVotes;
    }
}
