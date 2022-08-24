package com.hepo.c2c.social.govern.reviewer.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService;
import com.hepo.c2c.social.govern.reviewer.domain.ReviewerTaskStatus;
import com.hepo.c2c.social.govern.reviewer.mapper.ReviewerMapper;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.ArrayList;
import java.util.List;

/**
 * 评审员接口实现类
 *
 * @author linhaibo
 */
@DubboService(interfaceClass = ReviewerService.class, cluster = "failfast")
public class ReviewerServiceImpl extends ServiceImpl<ReviewerMapper, ReviewerTaskStatus> implements ReviewerService {

    /**
     * 选择评审员
     *
     * @param reportTaskId 举报任务id
     * @return 评审员列表
     */
    @Override
    public List<Long> selectReviewers(Long reportTaskId) {
        // 模拟通过算法选择一批评审员
        System.out.println("test环境：模拟通过算法选择一批评审员");
        List<Long> reviewerIds = new ArrayList<Long>();
        reviewerIds.add(1L);
        reviewerIds.add(2L);
        reviewerIds.add(3L);
        reviewerIds.add(4L);
        reviewerIds.add(5L);

        // 把每个评审员要执行的任务录入数据库
        for(Long reviewerId : reviewerIds) {
            ReviewerTaskStatus reviewerTaskStatus = new ReviewerTaskStatus();
            reviewerTaskStatus.setReviewerId(reviewerId);
            reviewerTaskStatus.setReportTaskId(reportTaskId);
            reviewerTaskStatus.setStatus(ReviewerTaskStatus.PROCESSING);
            baseMapper.insert(reviewerTaskStatus);
        }
        return reviewerIds;
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
