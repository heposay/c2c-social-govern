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

@ApiModel(value="")
@Data
@NoArgsConstructor
@TableName(value = "tb_user")
public class User implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 手机号码
     */
    @TableField(value = "phone")
    @ApiModelProperty(value="手机号码")
    private String phone;

    /**
     * 密码，加密存储
     */
    @TableField(value = "`password`")
    @ApiModelProperty(value="密码，加密存储")
    private String password;

    /**
     * 昵称，默认是用户id
     */
    @TableField(value = "nick_name")
    @ApiModelProperty(value="昵称，默认是用户id")
    private String nickName;

    /**
     * 人物头像
     */
    @TableField(value = "icon")
    @ApiModelProperty(value="人物头像")
    private String icon;

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

    public static final String COL_PHONE = "phone";

    public static final String COL_PASSWORD = "password";

    public static final String COL_NICK_NAME = "nick_name";

    public static final String COL_ICON = "icon";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}