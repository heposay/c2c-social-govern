package com.hepo.c2c.social.govern.mall.controller;

import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.service.IFollowService;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 关注表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/follow")
public class FollowController {

    @Resource
    private IFollowService followService;

    /**
     * 关注/取关
     * @param followUserId
     * @param isFollow
     * @return
     */
    @PostMapping("/{id}/{isFollow}")
    public ResultObject<String> follow(@PathVariable("id") Long followUserId,
                                       @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }

    /**
     * 是否关注
     * @param followUserId 被关注的用户id
     */
    @GetMapping("/or/not/{id}")
    public ResultObject<Boolean> isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    /**
     * 共同关注
     * @param id
     * @return
     */
    @GetMapping("/common/{id}")
    public ResultObject<List<UserDTO>> followCommons(@PathVariable("id") Long id) {
        return followService.followCommons(id);
    }
}
