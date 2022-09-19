package com.hepo.c2c.social.govern.mall.controller;

import cn.hutool.http.server.HttpServerRequest;
import com.hepo.c2c.social.govern.mall.domain.UserInfo;
import com.hepo.c2c.social.govern.mall.dto.LoginDTO;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.service.IUserInfoService;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.time.LocalDateTime;

import static com.hepo.c2c.social.govern.mall.utils.SystemConstants.GENDER_MALE;

/**
 * User表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IUserInfoService userInfoService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 发送手机验证码
     *
     * @param phone       手机号
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
     *
     * @param loginDTO    用户登录所需字段
     * @param httpSession
     * @return
     */
    @PostMapping("/login")
    public ResultObject<String> login(@RequestBody LoginDTO loginDTO, HttpSession httpSession) {
        return userService.login(loginDTO, httpSession);
    }

    /**
     * 退出登录
     *
     * @return
     */
    @PostMapping("/logout")
    public ResultObject<String> logout() {
        return userService.logout(request);
    }

    /**
     * 获取当前用户
     *
     * @return
     */
    @GetMapping("/me")
    public ResultObject<UserDTO> me() {
        UserDTO user = UserHolder.getUser();
        return ResultObject.success(user);
    }

    /**
     * 查看用户详情信息
     *
     * @param userId 用户id
     * @return
     */
    @GetMapping("/info/{id}")
    public ResultObject<UserInfo> info(@PathVariable("id") Long userId) {
        return userInfoService.info(userId);
    }

    /**
     * 用户签到
     * @return
     */
    @PostMapping("/sign")
    public ResultObject<String> sign() {
        return userService.sign();
    }


    /**
     * 签到统计
     * @return
     */
    @PostMapping("/sign/count")
    public ResultObject<Integer> signCount() {
        return userService.signCount();
    }
}
