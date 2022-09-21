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
/**
    * 秒杀优惠券表，与优惠券是一对一关系
    */
@ApiModel(value="秒杀优惠券表，与优惠券是一对一关系")
@Data
@NoArgsConstructor
@TableName(value = "tb_seckill_voucher")
public class SeckillVoucher implements Serializable {
    /**
     * 关联的优惠券的id
     */
    @TableId(value = "voucher_id", type = IdType.AUTO)
    @ApiModelProperty(value="关联的优惠券的id")
    private Long voucherId;

    /**
     * 库存
     */
    @TableField(value = "stock")
    @ApiModelProperty(value="库存")
    private Integer stock;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    @ApiModelProperty(value="创建时间")
    private LocalDateTime createTime;

    /**
     * 生效时间
     */
    @TableField(value = "begin_time")
    @ApiModelProperty(value="生效时间")
    private LocalDateTime beginTime;

    /**
     * 失效时间
     */
    @TableField(value = "end_time")
    @ApiModelProperty(value="失效时间")
    private LocalDateTime endTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    @ApiModelProperty(value="更新时间")
    private LocalDateTime updateTime;

    private static final long serialVersionUID = 1L;

    public static final String COL_VOUCHER_ID = "voucher_id";

    public static final String COL_STOCK = "stock";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_BEGIN_TIME = "begin_time";

    public static final String COL_END_TIME = "end_time";

    public static final String COL_UPDATE_TIME = "update_time";
}