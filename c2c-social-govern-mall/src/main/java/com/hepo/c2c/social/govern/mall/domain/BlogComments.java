package com.hepo.c2c.social.govern.mall.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * Description: 
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author  linhaibo
 */
@ApiModel(value="tb_blog_comments")
@Data
@NoArgsConstructor
@TableName(value = "tb_blog_comments")
public class BlogComments implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="用户id")
    private Long userId;

    /**
     * 探店id
     */
    @TableField(value = "blog_id")
    @ApiModelProperty(value="探店id")
    private Long blogId;

    /**
     * 关联的1级评论id，如果是一级评论，则值为0
     */
    @TableField(value = "parent_id")
    @ApiModelProperty(value="关联的1级评论id，如果是一级评论，则值为0")
    private Long parentId;

    /**
     * 回复的评论id
     */
    @TableField(value = "answer_id")
    @ApiModelProperty(value="回复的评论id")
    private Long answerId;

    /**
     * 回复的内容
     */
    @TableField(value = "content")
    @ApiModelProperty(value="回复的内容")
    private String content;

    /**
     * 点赞数
     */
    @TableField(value = "liked")
    @ApiModelProperty(value="点赞数")
    private Integer liked;

    /**
     * 状态，0：正常，1：被举报，2：禁止查看
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="状态，0：正常，1：被举报，2：禁止查看")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_BLOG_ID = "blog_id";

    public static final String COL_PARENT_ID = "parent_id";

    public static final String COL_ANSWER_ID = "answer_id";

    public static final String COL_CONTENT = "content";

    public static final String COL_LIKED = "liked";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}