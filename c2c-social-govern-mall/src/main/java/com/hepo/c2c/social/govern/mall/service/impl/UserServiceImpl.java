package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.mapper.UserMapper;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.utils.RegexUtils;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Random;

/**
 * 用户实现类
 *
 * @author linhaibo
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public ResultObject<String> sendCode(String phone, HttpSession httpSesion) {
        //1.校验手机是否合法

        boolean isPhone = RegexUtils.isPhoneInvalid(phone);
        if (!isPhone) {
            return ResultObject.error("请输入正确的手机号码！");
        }

        //2.生成6为随机验证码
        int code = new Random(6).nextInt();

        //3.保存验证码到session
        httpSesion.setAttribute("code", code);

        stringRedisTemplate.opsForValue().set("user:code:" + phone, String.valueOf(code));
        return ResultObject.success("验证码为：" + code);
    }

    public static void main(String[] args) {
        Integer code = RandomUtil.randomInt(6);
        System.out.println(code);
    }
}
