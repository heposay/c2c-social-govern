package com.hepo.c2c.social.govern.reward.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName(value = "reward_coin")
public class RewardCoin {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "reviewer_id")
    private Long reviewerId;

    @TableField(value = "coins")
    private Integer coins;

    public static final String COL_ID = "id";

    public static final String COL_REVIEWER_ID = "reviewer_id";

    public static final String COL_COINS = "coins";
}