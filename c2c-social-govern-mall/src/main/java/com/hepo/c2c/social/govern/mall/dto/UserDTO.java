package com.hepo.c2c.social.govern.mall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户简化信息
 *
 * @author linhaibo
 */
@Data
public class UserDTO implements Serializable {

    private String id;
    private String phone;
    private String nickName;
    private String icon;

}
