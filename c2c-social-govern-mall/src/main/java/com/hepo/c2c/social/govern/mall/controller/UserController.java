package com.hepo.c2c.social.govern.mall.controller;

import com.hepo.c2c.social.govern.mall.service.IUserInfoService;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * User表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Autowired
    private IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     *
     * @param phone      手机号
     * @param httpSesion 会话
     * @return
     */
    @PostMapping("/code")
    public ResultObject<String> code(String phone, HttpSession httpSesion) {
        //发送短信验证码并保存验证码
        return userService.sendCode(phone, httpSesion);
    }

}
