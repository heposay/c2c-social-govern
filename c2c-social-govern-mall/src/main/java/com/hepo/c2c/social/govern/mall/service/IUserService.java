package com.hepo.c2c.social.govern.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.vo.ResultObject;

import javax.servlet.http.HttpSession;

/**
 * User Service层接口
 *
 * @author linhaibo
 */
public interface IUserService extends IService<User>{


    /**
     * 发送验证码
     * @param phone
     * @param httpSesion
     * @return 验证码
     */
    ResultObject<String> sendCode(String phone, HttpSession httpSesion);
}
