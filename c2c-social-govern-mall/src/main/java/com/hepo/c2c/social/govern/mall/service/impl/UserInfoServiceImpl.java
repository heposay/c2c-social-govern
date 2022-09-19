package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.UserInfo;
import com.hepo.c2c.social.govern.mall.mapper.UserInfoMapper;
import com.hepo.c2c.social.govern.mall.service.IUserInfoService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.*;
import static com.hepo.c2c.social.govern.mall.utils.SystemConstants.GENDER_MALE;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultObject<UserInfo> info(Long userId) {
        String redisKey = USER_INFO_KEY + userId;
        String userInfoJson = stringRedisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isBlank(userInfoJson)) {
            UserInfo userInfo = super.getById(userId);
            if (userInfo == null) {
                userInfo = createUserInfo(userId);
                stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(userInfo));
                //设置有效期,默认1天
                stringRedisTemplate.expire(redisKey, USER_INFO_TTL, TimeUnit.DAYS);
                return ResultObject.success(userInfo);
            }
        }

        return ResultObject.success(JSONUtil.toBean(userInfoJson, UserInfo.class));
    }

    private UserInfo createUserInfo(Long userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setGender(GENDER_MALE);
        userInfo.setLevel(0);
        userInfo.setCredits(0);
        userInfo.setFans(0);
        userInfo.setFollowee(0);
        userInfo.setCreateTime(LocalDateTime.now());
        userInfo.setUpdateTime(LocalDateTime.now());
        save(userInfo);
        return userInfo;
    }
}
