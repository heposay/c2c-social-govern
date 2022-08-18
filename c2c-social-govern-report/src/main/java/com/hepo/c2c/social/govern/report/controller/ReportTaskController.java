package com.hepo.c2c.social.govern.report.controller;

import com.hepo.c2c.social.govern.report.service.ReportTaskService;
import com.hepo.c2c.social.govern.report.service.ReportTaskVoteService;
import com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService;
import com.hepo.c2c.social.govern.reward.api.service.RewardService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (report_task)表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/report_task")
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


    @DubboReference(url = "dubbo://localhost:20770", group = "DEFAULT_GROUP", interfaceClass = ReviewerService.class, cluster = "failfast")
    private ReviewerService reviewerService;

    @DubboReference(url = "dubbo://localhost:20990", group = "DEFAULT_GROUP", interfaceClass = RewardService.class, cluster = "failfast")
    private RewardService rewardService;


    @GetMapping("test")
    public String test(String name) {
        return rewardService.testRPC(name);
    }

}
