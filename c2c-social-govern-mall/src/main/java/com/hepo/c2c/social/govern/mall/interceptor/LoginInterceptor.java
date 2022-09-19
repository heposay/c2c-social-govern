package com.hepo.c2c.social.govern.mall.interceptor;

import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户登录拦截器
 *
 * @author linhaibo
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断是否拦截
        if (UserHolder.getUser() == null) {
            //需要拦截
            response.setStatus(401);
            return false;
        }
        //由用户则放行
        return true;
    }
}
