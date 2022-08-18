package com.hepo.c2c.social.govern.report.service.impl;

import com.hepo.c2c.social.govern.report.domain.ReportTask;
import com.hepo.c2c.social.govern.report.mapper.ReportTaskMapper;
import com.hepo.c2c.social.govern.report.service.ReportTaskService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * 举报任务接口实现类
 *
 * @author linhaibo
 */
@Service
public class ReportTaskServiceImpl extends ServiceImpl<ReportTaskMapper, ReportTask> implements ReportTaskService {

    @Override
    public ReportTask getById(Long id) {
        return baseMapper.selectById(id);
    }
}
