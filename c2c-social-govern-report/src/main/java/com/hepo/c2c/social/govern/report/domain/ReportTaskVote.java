package com.hepo.c2c.social.govern.report.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 举报任务投票实体类
 * @author linhaibo
 */
@ApiModel(value="举报任务投票实体类")
@Data
@NoArgsConstructor
@TableName(value = "report_task_vote")
public class ReportTaskVote {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Integer id;

    @TableField(value = "reviewer_id")
    @ApiModelProperty(value="")
    private Integer reviewerId;

    @TableField(value = "report_task_id")
    @ApiModelProperty(value="")
    private Integer reportTaskId;

    @TableField(value = "vote_result")
    @ApiModelProperty(value="")
    private Integer voteResult;

    public static final String COL_ID = "id";

    public static final String COL_REVIEWER_ID = "reviewer_id";

    public static final String COL_REPORT_TASK_ID = "report_task_id";

    public static final String COL_VOTE_RESULT = "vote_result";
}