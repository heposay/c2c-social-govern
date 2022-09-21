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
@ApiModel(value="tb_follow")
@Data
@NoArgsConstructor
@TableName(value = "tb_follow")
public class Follow implements Serializable {
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
     * 关联的用户id
     */
    @TableField(value = "follow_user_id")
    @ApiModelProperty(value="关联的用户id")
    private Long followUserId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private LocalDateTime createTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_FOLLOW_USER_ID = "follow_user_id";

    public static final String COL_CREATE_TIME = "create_time";
}