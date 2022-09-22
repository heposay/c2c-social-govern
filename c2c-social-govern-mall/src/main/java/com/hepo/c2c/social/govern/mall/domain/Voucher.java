package com.hepo.c2c.social.govern.mall.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * Description: 
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author  linhaibo
 */
@ApiModel(value="tb_voucher")
@Data
@NoArgsConstructor
@TableName(value = "tb_voucher")
public class Voucher implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 商铺id
     */
    @TableField(value = "shop_id")
    @ApiModelProperty(value="商铺id")
    private Long shopId;

    /**
     * 代金券标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value="代金券标题")
    private String title;

    /**
     * 副标题
     */
    @TableField(value = "sub_title")
    @ApiModelProperty(value="副标题")
    private String subTitle;

    /**
     * 使用规则
     */
    @TableField(value = "rules")
    @ApiModelProperty(value="使用规则")
    private String rules;

    /**
     * 支付金额，单位是分。例如200代表2元
     */
    @TableField(value = "pay_value")
    @ApiModelProperty(value="支付金额，单位是分。例如200代表2元")
    private Long payValue;

    /**
     * 抵扣金额，单位是分。例如200代表2元
     */
    @TableField(value = "actual_value")
    @ApiModelProperty(value="抵扣金额，单位是分。例如200代表2元")
    private Long actualValue;

    /**
     * 0,普通券；1,秒杀券
     */
    @TableField(value = "`type`")
    @ApiModelProperty(value="0,普通券；1,秒杀券")
    private Integer type;

    /**
     * 1,上架; 2,下架; 3,过期
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="1,上架; 2,下架; 3,过期")
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

    /**
     * 库存
     */
    @TableField(exist = false)
    private Integer stock;

    /**
     * 生效时间
     */
    @TableField(exist = false)
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    @TableField(exist = false)
    private LocalDateTime endTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_SHOP_ID = "shop_id";

    public static final String COL_TITLE = "title";

    public static final String COL_SUB_TITLE = "sub_title";

    public static final String COL_RULES = "rules";

    public static final String COL_PAY_VALUE = "pay_value";

    public static final String COL_ACTUAL_VALUE = "actual_value";

    public static final String COL_TYPE = "type";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}