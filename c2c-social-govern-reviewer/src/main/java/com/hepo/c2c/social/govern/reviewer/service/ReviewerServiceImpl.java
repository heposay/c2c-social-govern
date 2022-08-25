package com.hepo.c2c.social.govern.reviewer.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService;
import com.hepo.c2c.social.govern.reviewer.domain.ReviewerTaskStatus;
import com.hepo.c2c.social.govern.reviewer.mapper.ReviewerMapper;
import org.apache.dubbo.config.annotation.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 评审员接口实现类
 *
 * @author linhaibo
 */
@Service(version = "1.0.0", interfaceClass = ReviewerService.class, cluster = "failfast")
//@ApiModule(value = "评审员接口实现类", apiInterface = ReviewerService.class, version = "1.0")
public class ReviewerServiceImpl extends ServiceImpl<ReviewerMapper, ReviewerTaskStatus> implements ReviewerService {

    /**
     * 选择评审员
     *
     * @param reportTaskId 举报任务id
     * @return 评审员列表
     */
//    @ApiDoc(value = "选择评审员", description = "选出一批评审员的ID", version = "1.0", responseClassDescription = "返回一批审批员的ID")
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
        for (Long reviewerId : reviewerIds) {
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
//    @ApiDoc(value = "完成投票", description = "完成投票接口", version = "1.0", responseClassDescription = "完成投票")
    @Override
    public void finishVote(Long reviewerId, Long reportTaskId) {
        ReviewerTaskStatus taskStatus = new ReviewerTaskStatus();
        taskStatus.setStatus(ReviewerTaskStatus.FINISHED);
        LambdaUpdateWrapper<ReviewerTaskStatus> updateWrapper = new LambdaUpdateWrapper<ReviewerTaskStatus>()
                .eq(ReviewerTaskStatus::getReviewerId, reviewerId)
                .eq(ReviewerTaskStatus::getReportTaskId, reportTaskId);
        baseMapper.update(taskStatus, updateWrapper);
    }
}
