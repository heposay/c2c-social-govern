package com.hepo.c2c.social.govern.mall.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Description:  商品库存
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-29 16:17
 *
 * @author linhaibo
 */
@ApiModel(value = "商品库存")
@Data
@NoArgsConstructor
@TableName(value = "tb_item_stock")
public class ItemStock implements Serializable {
    /**
     * 商品id，关联tb_item表
     */
    @TableId(value = "item_id", type = IdType.AUTO)
    @ApiModelProperty(value = "商品id，关联tb_item表")
    private Long itemId;

    /**
     * 商品库存
     */
    @TableField(value = "stock")
    @ApiModelProperty(value = "商品库存")
    private Integer stock;

    /**
     * 商品销量
     */
    @TableField(value = "sold")
    @ApiModelProperty(value = "商品销量")
    private Integer sold;

    private static final long serialVersionUID = 1L;

    public static final String COL_ITEM_ID = "item_id";

    public static final String COL_STOCK = "stock";

    public static final String COL_SOLD = "sold";
}