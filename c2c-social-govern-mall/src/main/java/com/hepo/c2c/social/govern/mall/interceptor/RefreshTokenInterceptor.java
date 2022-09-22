package com.hepo.c2c.social.govern.mall.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.LOGIN_USER_TTL;

/**
 * 将用户信息保存在threadLocal变量中，方便后期使用
 * @author linhaibo
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头的token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }

        //2.基于token获取redis用户
        String redisKey = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(redisKey);

        //3.判断用户是否存在
        if (CollectionUtil.isEmpty(userMap)) {
            return true;
        }

        //4.将查询到的hash数据转化成UserDTO对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        //5.将变量放到ThreadLocal
        UserHolder.saveUser(userDTO);
        //6.刷新token有效期
        stringRedisTemplate.expire(redisKey, LOGIN_USER_TTL, TimeUnit.DAYS);

        //7.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //请求结束后，要删除ThreadLocal里面的变量，避免内存泄漏
        UserHolder.removeUser();
    }
}
