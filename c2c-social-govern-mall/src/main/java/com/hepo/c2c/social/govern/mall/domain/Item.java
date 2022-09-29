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
 * Description:  商品表
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-29 16:17
 *
 * @author linhaibo
 */
@ApiModel(value = "商品表")
@Data
@NoArgsConstructor
@TableName(value = "tb_item")
public class Item implements Serializable {
    /**
     * 商品id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "商品id")
    private Long id;

    /**
     * 商品标题
     */
    @TableField(value = "title")
    @ApiModelProperty(value = "商品标题")
    private String title;

    /**
     * 商品名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value = "商品名称")
    private String name;

    /**
     * 价格（分）
     */
    @TableField(value = "price")
    @ApiModelProperty(value = "价格（分）")
    private Long price;

    /**
     * 商品图片
     */
    @TableField(value = "image")
    @ApiModelProperty(value = "商品图片")
    private String image;

    /**
     * 类目名称
     */
    @TableField(value = "category")
    @ApiModelProperty(value = "类目名称")
    private String category;

    /**
     * 品牌名称
     */
    @TableField(value = "brand")
    @ApiModelProperty(value = "品牌名称")
    private String brand;

    /**
     * 规格
     */
    @TableField(value = "spec")
    @ApiModelProperty(value = "规格")
    private String spec;

    /**
     * 商品状态 1-正常，2-下架，3-删除
     */
    @TableField(value = "`status`")
    @ApiModelProperty(value = "商品状态 1-正常，2-下架，3-删除")
    private Integer status;

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

    public static final String COL_ID = "id";

    public static final String COL_TITLE = "title";

    public static final String COL_NAME = "name";

    public static final String COL_PRICE = "price";

    public static final String COL_IMAGE = "image";

    public static final String COL_CATEGORY = "category";

    public static final String COL_BRAND = "brand";

    public static final String COL_SPEC = "spec";

    public static final String COL_STATUS = "status";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}