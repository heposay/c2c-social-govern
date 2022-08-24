package com.hepo.c2c.social.govern.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hepo.c2c.social.govern.report.domain.ReportTaskVote;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 举报任务投票Mapper
 *
 * @author linhaibo
 */
public interface ReportTaskVoteMapper extends BaseMapper<ReportTaskVote> {
    /**
     * 批量更新
     *
     * @param list 待更新内容
     * @return 更新条数
     */
    int updateBatch(List<ReportTaskVote> list);

    /**
     * 批量新增
     *
     * @param list 待新增的内容
     * @return 新增条数
     */
    int batchInsert(@Param("list") List<ReportTaskVote> list);

    /**
     * 始化这批评审员对举报任务的投票状态
     *
     * @param reviewerIds 评审员ID集合
     * @param taskId      举报任务ID
     */
    void initVotes(List<Long> reviewerIds, Long taskId);
}