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
 * Description:
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
@ApiModel(value = "tb_blog")
@Data
@NoArgsConstructor
@TableName(value = "tb_blog")
public class Blog implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private Long id;

    /**
     * 商户id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value = "商户id")
    private Long shopId;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 探店的照片，最多9张，多张以","隔开
     */
    @TableField(value = "images")
    @ApiModelProperty(value = "探店的照片，最多9张，多张以','隔开")
    private String images;

    /**
     * 探店的文字描述
     */
    @TableField(value = "content")
    @ApiModelProperty(value = "探店的文字描述")
    private String content;

    /**
     * 点赞数量
     */
    @TableField(value = "liked")
    @ApiModelProperty(value = "点赞数量")
    private Integer liked;

    /**
     * 评论数量
     */
    @TableField(value = "comments")
    @ApiModelProperty(value = "评论数量")
    private Integer comments;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;

    /**
     * 用户姓名
     */
    @TableField(exist = false)
    private String nickname;

    /**
     * 用户头像
     */
    @TableField(exist = false)
    private String icon;

    /**
     * 是否被点赞
     */
    @TableField(exist = false)
    private Boolean isLike;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_TITLE = "title";

    public static final String COL_IMAGES = "images";

    public static final String COL_CONTENT = "content";

    public static final String COL_LIKED = "liked";

    public static final String COL_COMMENTS = "comments";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}