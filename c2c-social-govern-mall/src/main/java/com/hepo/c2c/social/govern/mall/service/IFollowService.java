package com.hepo.c2c.social.govern.mall.service;

import com.hepo.c2c.social.govern.mall.domain.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.vo.ResultObject;

import java.util.List;

/**
 * Description:  关注列表接口
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
public interface IFollowService extends IService<Follow> {


    ResultObject<String> follow(Long followUserId, Boolean isFollow);

    ResultObject<Boolean> isFollow(Long followUserId);

    ResultObject<List<UserDTO>> followCommons(Long id);
}
