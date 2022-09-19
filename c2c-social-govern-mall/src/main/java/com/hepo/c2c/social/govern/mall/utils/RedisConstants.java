package com.hepo.c2c.social.govern.mall.utils;

/**
 * redis key的变量
 *
 * @author linhaibo
 */
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";

    public static final Long LOGIN_CODE_TTL = 30L;


    public static final String LOGIN_USER_KEY = "login:user:";

    public static final Long LOGIN_USER_TTL = 30L;

    public static final String USER_INFO_KEY = "user:info:";

    public static final Long USER_INFO_TTL = 1L;


    public static final String USER_SIGN_KEY = "user:sign:";

}
