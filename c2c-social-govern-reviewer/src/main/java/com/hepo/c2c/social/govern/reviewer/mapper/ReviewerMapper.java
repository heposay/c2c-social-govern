package com.hepo.c2c.social.govern.reviewer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hepo.c2c.social.govern.reviewer.domain.ReviewerTaskStatus;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 评审员Mapper接口
 *
 * @author linhaibo
 */
public interface ReviewerMapper extends BaseMapper<ReviewerTaskStatus> {

}