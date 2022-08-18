package com.hepo.c2c.social.govern.report.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hepo.c2c.social.govern.report.domain.ReportTask;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 举报任务Mapper
 *
 * @author linhaibo
 */
public interface ReportTaskMapper extends BaseMapper<ReportTask> {
    /**
     * 批量更新
     *
     * @param list 待更新内容
     * @return 更新条数
     */
    int updateBatch(List<ReportTask> list);


    /**
     * 批量新增
     *
     * @param list 待新增的内容
     * @return 新增条数
     */
    int batchInsert(@Param("list") List<ReportTask> list);
}