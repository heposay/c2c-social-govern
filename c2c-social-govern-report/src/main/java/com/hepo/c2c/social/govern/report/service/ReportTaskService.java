package com.hepo.c2c.social.govern.report.service;

import com.hepo.c2c.social.govern.report.domain.ReportTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 举报任务接口
 *
 * @author linhaibo
 */
public interface ReportTaskService extends IService<ReportTask> {


    /**
     * 根据id查询举报任务
     *
     * @param id 举报任务id
     * @return 举报任务实体
     */
    ReportTask getById(Long id);

}
