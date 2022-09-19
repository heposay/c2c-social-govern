package com.hepo.c2c.social.govern.mall.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "")
@Data
@NoArgsConstructor
@TableName(value = "tb_user_info")
public class UserInfo implements Serializable {
    /**
     * 主键，用户id
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty(value = "主键，用户id")
    private Long userId;

    /**
     * 城市名称
     */
    @TableField(value = "city")
    @ApiModelProperty(value = "城市名称")
    private String city;

    /**
     * 个人介绍，不要超过128个字符
     */
    @TableField(value = "introduce")
    @ApiModelProperty(value = "个人介绍，不要超过128个字符")
    private String introduce;

    /**
     * 粉丝数量
     */
    @TableField(value = "fans")
    @ApiModelProperty(value = "粉丝数量")
    private Integer fans;

    /**
     * 关注的人的数量
     */
    @TableField(value = "followee")
    @ApiModelProperty(value = "关注的人的数量")
    private Integer followee;

    /**
     * 性别，0：男，1：女
     */
    @TableField(value = "gender")
    @ApiModelProperty(value = "性别，0：男，1：女")
    private Integer gender;

    /**
     * 生日
     */
    @TableField(value = "birthday")
    @ApiModelProperty(value = "生日")
    private LocalDate birthday;

    /**
     * 积分
     */
    @TableField(value = "credits")
    @ApiModelProperty(value = "积分")
    private Integer credits;

    /**
     * 会员级别，0~9级,0代表未开通会员
     */
    @TableField(value = "`level`")
    @ApiModelProperty(value = "会员级别，0~9级,0代表未开通会员")
    private Integer level;

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

    private static final long serialVersionUID = 1L;

    public static final String COL_USER_ID = "user_id";

    public static final String COL_CITY = "city";

    public static final String COL_INTRODUCE = "introduce";

    public static final String COL_FANS = "fans";

    public static final String COL_FOLLOWEE = "followee";

    public static final String COL_GENDER = "gender";

    public static final String COL_BIRTHDAY = "birthday";

    public static final String COL_CREDITS = "credits";

    public static final String COL_LEVEL = "level";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}