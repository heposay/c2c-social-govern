package com.hepo.c2c.social.govern.reviewer.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "reviewer_task_status")
public class ReviewerTaskStatus {

    /**
     * 处理中
     */
    public static final Integer PROCESSING = 1;
    /**
     * 处理完毕
     */
    public static final Integer FINISHED = 2;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "reviewer_id")
    private Long reviewerId;

    @TableField(value = "report_task_id")
    private Long reportTaskId;

    @TableField(value = "`status`")
    private Integer status;

    public static final String COL_ID = "id";

    public static final String COL_REVIEWER_ID = "reviewer_id";

    public static final String COL_REPORT_TASK_ID = "report_task_id";

    public static final String COL_STATUS = "status";
}