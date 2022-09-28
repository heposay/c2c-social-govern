package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.Follow;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.mapper.FollowMapper;
import com.hepo.c2c.social.govern.mall.service.IFollowService;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.FOLLOWS_USER_KEY;

/**
 * Description: 关注列表实现类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IUserService userService;

    @Override
    public ResultObject<String> follow(Long followUserId, Boolean isFollow) {
        //1.获取用户
        UserDTO user = UserHolder.getUser();
        String key = FOLLOWS_USER_KEY + user.getId();
        //2.判断是关注还是取关
        if (isFollow) {
            Long count = query().eq("user_id", user.getId()).eq("follow_user_id", followUserId).count();
            if (count == 0) {
                //关注，新增数据
                Follow follow = new Follow();
                follow.setFollowUserId(followUserId);
                follow.setUserId(Long.valueOf(user.getId()));
                boolean isSuccess = save(follow);
                if (isSuccess) {
                    // 把关注用户的id，放入redis的set集合 sadd userId followerUserId
                    stringRedisTemplate.opsForSet().add(key, followUserId.toString());
                }
            }
            return ResultObject.success("关注成功！");
        } else {
            //取关，删除数据
            boolean isSuccess = remove(new QueryWrapper<Follow>().eq("follow_user_id", followUserId).eq("user_id", user.getId()));
            if (isSuccess) {
                // 把关注用户的id从Redis集合中移除
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }
            return ResultObject.success("取关成功");
        }
    }

    @Override
    public ResultObject<Boolean> isFollow(Long followUserId) {
        UserDTO user = UserHolder.getUser();
        Long count = query().eq("user_id", user.getId()).eq("follow_user_id", followUserId).count();
        return ResultObject.success(count>0);
    }

    /**
     * 共同关注
     * @param id
     * @return
     */
    @Override
    public ResultObject<List<UserDTO>> followCommons(Long id) {
        //1.获取当前用户
        UserDTO user = UserHolder.getUser();
        String key = FOLLOWS_USER_KEY + user.getId();
        //2.求交集
        String key2 = FOLLOWS_USER_KEY + id;

        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if (intersect == null || intersect.isEmpty()) {
            return ResultObject.success(Collections.emptyList());
        }
        //3.解析id
        List<Long> userIds = intersect.stream().map(Long::parseLong).collect(Collectors.toList());
        //4.查询用户
        List<UserDTO> userDTOList = userService.listByIds(userIds).stream()
                .map(u -> BeanUtil.copyProperties(u, UserDTO.class)).collect(Collectors.toList());
        return ResultObject.success(userDTOList);
    }
}
