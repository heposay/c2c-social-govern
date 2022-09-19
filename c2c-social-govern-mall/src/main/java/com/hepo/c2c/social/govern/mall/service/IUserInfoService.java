package com.hepo.c2c.social.govern.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hepo.c2c.social.govern.mall.domain.UserInfo;
import com.hepo.c2c.social.govern.vo.ResultObject;

/**
 * UserInfo Service层接口
 *
 * @author linhaibo
 */
public interface IUserInfoService extends IService<UserInfo>{

    /**
     * 查看用户信息
     * @param userId
     * @return
     */
    ResultObject<UserInfo> info(Long userId);
}
