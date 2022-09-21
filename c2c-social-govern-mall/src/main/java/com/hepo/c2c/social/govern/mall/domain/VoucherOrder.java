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
@ApiModel(value="tb_voucher_order")
@Data
@NoArgsConstructor
@TableName(value = "tb_voucher_order")
public class VoucherOrder implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 下单的用户id
     */
    @TableField(value = "user_id")
    @ApiModelProperty(value="下单的用户id")
    private Long userId;

    /**
     * 购买的代金券id
     */
    @TableField(value = "voucher_id")
    @ApiModelProperty(value="购买的代金券id")
    private Long voucherId;

    /**
     * 支付方式 1：余额支付；2：支付宝；3：微信
     */
    @TableField(value = "pay_type")
    @ApiModelProperty(value="支付方式 1：余额支付；2：支付宝；3：微信")
    private Integer payType;

    /**
     * 订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value="订单状态，1：未支付；2：已支付；3：已核销；4：已取消；5：退款中；6：已退款")
    private Integer status;

    /**
     * 下单时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="下单时间")
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    @TableField(value = "pay_time")
    @ApiModelProperty(value="支付时间")
    private LocalDateTime payTime;

    /**
     * 核销时间
     */
    @TableField(value = "use_time")
    @ApiModelProperty(value="核销时间")
    private LocalDateTime useTime;

    /**
     * 退款时间
     */
    @TableField(value = "refund_time")
    @ApiModelProperty(value="退款时间")
    private LocalDateTime refundTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_USER_ID = "user_id";

    public static final String COL_VOUCHER_ID = "voucher_id";

    public static final String COL_PAY_TYPE = "pay_type";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_PAY_TIME = "pay_time";

    public static final String COL_USE_TIME = "use_time";

    public static final String COL_REFUND_TIME = "refund_time";

    public static final String COL_UPDATE_TIME = "update_time";
}