package com.hepo.c2c.social.govern.reward.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(value = "奖励金币")
@Data
@NoArgsConstructor
@TableName(value = "reward_coin")
public class RewardCoin {
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty(value = "id")
    private Integer id;

    @TableField(value = "reviewer_id")
    @ApiModelProperty(value = "评审员id")
    private Integer reviewerId;

    @TableField(value = "coins")
    @ApiModelProperty(value = "金币")
    private Integer coins;

    public static final String COL_ID = "id";

    public static final String COL_REVIEWER_ID = "reviewer_id";

    public static final String COL_COINS = "coins";
}