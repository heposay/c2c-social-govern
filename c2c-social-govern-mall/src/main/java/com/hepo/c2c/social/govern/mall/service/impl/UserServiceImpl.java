package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.dto.LoginDTO;
import com.hepo.c2c.social.govern.mall.mapper.UserMapper;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.utils.RegexUtils;
import com.hepo.c2c.social.govern.vo.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

import static com.hepo.c2c.social.govern.mall.utils.SystemConstants.USER_NICK_NAME_PREFIX;

/**
 * 用户实现类
 *
 * @author linhaibo
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public ResultObject<String> sendCode(String phone, HttpSession httpSesion) {
        //1.校验手机是否合法

        boolean isPhone = RegexUtils.isPhoneInvalid(phone);
        if (isPhone) {
            return ResultObject.error("输入的手机号格式有误！");
        }

        //2.生成6为随机验证码
        String code = RandomUtil.randomNumbers(6);

        //3.保存验证码到session
        httpSesion.setAttribute("code", code);

        //4.发送验证码
        log.info("收到的验证码为:{}, 请快速登录", code);
        return ResultObject.success("验证码为：" + code);
    }

    @Override
    public ResultObject<User> login(LoginDTO loginDTO, HttpSession httpSession) {
        //1.校验手机号码和验证码
        String phone = loginDTO.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultObject.error("输入的手机号格式有误！");
        }
        Object cacheCode = httpSession.getAttribute("code");
        String code = loginDTO.getCode();
        if (cacheCode == null || cacheCode.toString().equals(code)) {
            return ResultObject.error("验证码错误");
        }
        //2.根据手机号码查询用户
        User user = query().eq("phone", phone).one();
        //2.1查不到用户，创建用户并保存到session，最后返回
        if (Objects.isNull(user)) {
            user = createUserWithPhone(phone);

        }
        //2.2查到用户，保存到session并返回
        httpSession.setAttribute("login:user:", user);
        return ResultObject.success(user);
    }

    /**
     * 创建用户并保存
     *
     * @param phone
     */
    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        save(user);
        return user;
    }

}
