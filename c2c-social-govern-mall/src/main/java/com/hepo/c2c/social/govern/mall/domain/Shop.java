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
@TableName(value = "tb_shop")
public class Shop implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value="主键")
    private Long id;

    /**
     * 商铺名称
     */
    @TableField(value = "`name`")
    @ApiModelProperty(value="商铺名称")
    private String name;

    /**
     * 商铺类型的id
     */
    @TableField(value = "type_id")
    @ApiModelProperty(value="商铺类型的id")
    private Long typeId;

    /**
     * 商铺图片，多个图片以','隔开
     */
    @TableField(value = "images")
    @ApiModelProperty(value="商铺图片，多个图片以','隔开")
    private String images;

    /**
     * 商圈，例如陆家嘴
     */
    @TableField(value = "area")
    @ApiModelProperty(value="商圈，例如陆家嘴")
    private String area;

    /**
     * 地址
     */
    @TableField(value = "address")
    @ApiModelProperty(value="地址")
    private String address;

    /**
     * 经度
     */
    @TableField(value = "x")
    @ApiModelProperty(value="经度")
    private Double x;

    /**
     * 维度
     */
    @TableField(value = "y")
    @ApiModelProperty(value="维度")
    private Double y;

    /**
     * 均价，取整数
     */
    @TableField(value = "avg_price")
    @ApiModelProperty(value="均价，取整数")
    private Long avgPrice;

    /**
     * 销量
     */
    @TableField(value = "sold")
    @ApiModelProperty(value="销量")
    private Integer sold;

    /**
     * 评论数量
     */
    @TableField(value = "comments")
    @ApiModelProperty(value="评论数量")
    private Integer comments;

    /**
     * 评分，1~5分，乘10保存，避免小数
     */
    @TableField(value = "score")
    @ApiModelProperty(value="评分，1~5分，乘10保存，避免小数")
    private Integer score;

    /**
     * 营业时间，例如 10:00-22:00
     */
    @TableField(value = "open_hours")
    @ApiModelProperty(value="营业时间，例如 10:00-22:00")
    private String openHours;

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


    @TableField(exist = false)
    private Double distance;

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_NAME = "name";

    public static final String COL_TYPE_ID = "type_id";

    public static final String COL_IMAGES = "images";

    public static final String COL_AREA = "area";

    public static final String COL_ADDRESS = "address";

    public static final String COL_X = "x";

    public static final String COL_Y = "y";

    public static final String COL_AVG_PRICE = "avg_price";

    public static final String COL_SOLD = "sold";

    public static final String COL_COMMENTS = "comments";

    public static final String COL_SCORE = "score";

    public static final String COL_OPEN_HOURS = "open_hours";

    public static final String COL_CREATE_TIME = "create_time";

    public static final String COL_UPDATE_TIME = "update_time";
}