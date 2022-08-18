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
 * 举报任务实体类
 * @author linhaibo
 */
@ApiModel(value="举报任务实体类")
@Data
@NoArgsConstructor
@TableName(value = "report_task")
public class ReportTask {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="")
    private Integer id;

    @TableField(value = "`type`")
    @ApiModelProperty(value="")
    private String type;

    @TableField(value = "report_user_id")
    @ApiModelProperty(value="")
    private Integer reportUserId;

    @TableField(value = "report_content")
    @ApiModelProperty(value="")
    private String reportContent;

    @TableField(value = "target_id")
    @ApiModelProperty(value="")
    private Integer targetId;

    @TableField(value = "vote_result")
    @ApiModelProperty(value="")
    private Integer voteResult;

    public static final String COL_ID = "id";

    public static final String COL_TYPE = "type";

    public static final String COL_REPORT_USER_ID = "report_user_id";

    public static final String COL_REPORT_CONTENT = "report_content";

    public static final String COL_TARGET_ID = "target_id";

    public static final String COL_VOTE_RESULT = "vote_result";
}