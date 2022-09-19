package com.hepo.c2c.social.govern.mall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录DTO
 *
 * @author linhaibo
 */
@Data
public class LoginDTO implements Serializable {

    private String phone;

    private String code;

    private String password;
}
