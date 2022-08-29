package com.hepo.c2c.social.govern.report.controller;

import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.DefaultBlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.DefaultBlockExceptionHandler;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.hepo.c2c.social.govern.report.domain.ReportTask;
import com.hepo.c2c.social.govern.report.domain.ReportTaskVote;
import com.hepo.c2c.social.govern.report.service.ReportTaskService;
import com.hepo.c2c.social.govern.report.service.ReportTaskVoteService;
import com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService;
import com.hepo.c2c.social.govern.reward.api.service.RewardService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * (report_task)表控制层
 *
 * @author linhaibo
 */
@RestController
public class ReportTaskController {
    /**
     * 举报任务Service组件
     */
    @Autowired
    private ReportTaskService reportTaskService;

    /**
     * 举报任务投票Service组件
     */
    @Autowired
    private ReportTaskVoteService reportTaskVoteService;


    @Reference(version = "${dubbo.version}", interfaceClass = ReviewerService.class, cluster = "failfast")
    private ReviewerService reviewerService;

    @Reference(version = "${dubbo.version}", interfaceClass = RewardService.class, cluster = "failfast")
    private RewardService rewardService;


    @GetMapping("report")
    public ResultObject<String> report(ReportTask reportTask) {
        reportTask.setVoteResult(ReportTask.VOTE_RESULT_UNKNOWN);
        //在本地添加一个举报任务
        reportTaskService.save(reportTask);
        // 调用评审员服务，选择一批评审员
        List<Long> reviewerIds = selectReviewers(reportTask.getId());

        //在本地数据库初始化这批评审员对举报任务的投票状态
        if (reviewerIds != null) {
            reportTaskVoteService.initVotes(reviewerIds, reportTask.getId());
        }

        // 模拟发送push消息给评审员
        System.out.println("模拟发送push消息给评审员.....");

        return ResultObject.success("success");
    }

    @SentinelResource(value = "selectReviewers",
            blockHandlerClass = DefaultBlockRequestHandler.class,
            fallbackClass = DefaultBlockExceptionHandler.class)
    public List<Long> selectReviewers(Long taskId) {
        return reviewerService.selectReviewers(taskId);
    }

    /**
     * 查询举报任务
     *
     * @param id 举报任务id
     * @return
     */
    @GetMapping("/report/query/{id}")
    public ResultObject<ReportTask> queryById(@PathVariable("id") Long id) {
        return ResultObject.success(reportTaskService.getById(id));

    }

    @PostMapping("/report/vote")
    public ResultObject<String> vote(Long reviewerId, Long reportTaskId, Integer voteResult) {
        // 本地数据库记录投票
        reportTaskVoteService.vote(reviewerId, reportTaskId, voteResult);
        //调用评审员服务，标记本次投票结束
        reviewerService.finishVote(reviewerId, reportTaskId);
        // 对举报任务进行归票
        Boolean hasFinishVote = reportTaskVoteService.caculateVotes(reportTaskId);
        //如果举报任务得到归票结果
        if (hasFinishVote) {
            //发放奖励
            List<ReportTaskVote> reportTaskVotes = reportTaskVoteService.queryByReportTaskId(reportTaskId);
            //获取这些审批员的ID
            List<Long> reviewerIds = reportTaskVotes.stream().map(ReportTaskVote::getReviewerId).collect(Collectors.toList());
            rewardService.giveReward(reviewerIds);
            // 推送消息到MQ，告知其他系统，本次评审结果
            System.out.println("推送消息到MQ，告知其他系统，本次评审结果");
        }
        return ResultObject.success("success");
    }
}
