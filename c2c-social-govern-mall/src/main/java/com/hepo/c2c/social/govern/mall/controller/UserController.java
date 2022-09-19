package com.hepo.c2c.social.govern.mall.controller;

import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.dto.LoginDTO;
import com.hepo.c2c.social.govern.mall.service.IUserInfoService;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * @param httpSession 会话
     * @return
     */
    @PostMapping("/code")
    public ResultObject<String> code(String phone, HttpSession httpSession) {
        //发送短信验证码并保存验证码
        return userService.sendCode(phone, httpSession);
    }

    /**
     * 用户登录
     * @param loginDTO 用户登录所需字段
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public ResultObject<User> login(@RequestBody LoginDTO loginDTO, HttpSession httpSession) {
        return userService.login(loginDTO, httpSession);
    }


}
