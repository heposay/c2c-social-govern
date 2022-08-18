package com.hepo.c2c.social.govern.reviewer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value="评审员任务状态")
@Data
@NoArgsConstructor
@TableName(value = "reviewer_task_status")
public class ReviewerTaskStatus {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Integer id;

    @TableField(value = "reviewer_id")
    @ApiModelProperty(value="评审员id")
    private Integer reviewerId;

    @TableField(value = "report_task_id")
    @ApiModelProperty(value="举报任务id")
    private Integer reportTaskId;

    @TableField(value = "`status`")
    @ApiModelProperty(value="状态")
    private Integer status;

    public static final String COL_ID = "id";

    public static final String COL_REVIEWER_ID = "reviewer_id";

    public static final String COL_REPORT_TASK_ID = "report_task_id";

    public static final String COL_STATUS = "status";
}