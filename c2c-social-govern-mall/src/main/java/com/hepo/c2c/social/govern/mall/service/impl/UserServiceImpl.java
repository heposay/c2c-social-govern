package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.dto.LoginDTO;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.mapper.UserMapper;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.utils.RedisConstants;
import com.hepo.c2c.social.govern.mall.utils.RegexUtils;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.swing.text.DateFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.*;
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
        //2.从redis获取key
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        if (StrUtil.isNotBlank(cacheCode)) {
            log.info("收到的验证码为:{}, 请快速登录", cacheCode);
            return ResultObject.success("验证码为：" + cacheCode);
        }

        //3.生成6为随机验证码
        String code = RandomUtil.randomNumbers(6);

        //4.保存验证码到redis,30分钟有效期
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        //5.发送验证码
        log.info("收到的验证码为:{}, 请快速登录", code);
        return ResultObject.success("验证码为：" + code);
    }

    @Override
    public ResultObject<String> login(LoginDTO loginDTO, HttpSession httpSession) {
        //1.校验手机号码
        String phone = loginDTO.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultObject.error("输入的手机号格式有误！");
        }
        //2.查询验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginDTO.getCode();
        if (StrUtil.isBlank(code)) {
            return ResultObject.error("验证码不能为空！");
        }
        if (cacheCode == null || !cacheCode.equals(code)) {
            return ResultObject.error("验证码错误！");
        }

        //3.根据手机号码查询用户
        User user = query().eq("phone", phone).one();
        if (Objects.isNull(user)) {
            //4.查不到用户，创建用户
            user = createUserWithPhone(phone);
        }
        //5.查到用户，保存信息到redis
        //5.1生成随机的token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        //5.2将user转化成map存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO);
        //5.3存储到redis
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        //5.4设置有效期
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        //6.返回token
        return ResultObject.success(token);
    }

    @Override
    public ResultObject<String> logout(HttpServletRequest request) {
        //1.判断当前用户是否登录
        UserDTO user = UserHolder.getUser();
        if (Objects.isNull(user)) {
            return ResultObject.error("当前用户未登录！");
        }
        //2.删除redis的用户信息
        String token = request.getHeader("authorization");
        String redisKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().delete(redisKey, BeanUtil.beanToMap(user).keySet().toArray());
        return ResultObject.success("成功退出登录");
    }

    @Override
    public ResultObject<String> sign() {
        //1.获取当前用户
        UserDTO user = UserHolder.getUser();
        //2.获取日期
        LocalDateTime now = LocalDateTime.now();
        //3.拼接redis key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String redisKey = USER_SIGN_KEY + user.getId() + keySuffix;
        //4.获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        //5.写入redis SETBIT key offset 1
        stringRedisTemplate.opsForValue().setBit(redisKey, dayOfMonth - 2, true);
        return ResultObject.success("签到成功");
    }

    @Override
    public ResultObject<Integer> signCount() {
        return ResultObject.success(0);
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
