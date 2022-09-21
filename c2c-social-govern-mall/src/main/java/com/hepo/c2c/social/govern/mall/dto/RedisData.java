package com.hepo.c2c.social.govern.mall.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Description: redis逻辑数据
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 14:30
 *
 * @author linhaibo
 */
@Data
public class RedisData implements Serializable {

    private Object data;

    private LocalDateTime expireTime;
}
