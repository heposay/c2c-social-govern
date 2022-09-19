package com.hepo.c2c.social.govern.mall.utils;

import com.hepo.c2c.social.govern.mall.dto.UserDTO;

/**
 * 存放用户信息工具类
 */
public class UserHolder {

    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user) {
        tl.set(user);
    }

    public static UserDTO getUser() {
        return tl.get();
    }

    public static void removeUser() {
        tl.remove();
    }
}
