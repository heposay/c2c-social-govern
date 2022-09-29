package com.hepo.c2c.social.govern.mall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.Blog;
import com.hepo.c2c.social.govern.mall.domain.Follow;
import com.hepo.c2c.social.govern.mall.domain.User;
import com.hepo.c2c.social.govern.mall.dto.ScrollResult;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.mapper.BlogMapper;
import com.hepo.c2c.social.govern.mall.service.IBlogService;
import com.hepo.c2c.social.govern.mall.service.IFollowService;
import com.hepo.c2c.social.govern.mall.service.IUserService;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;
import java.util.stream.Collectors;

import static com.hepo.c2c.social.govern.mall.utils.RedisConstants.*;
import static com.hepo.c2c.social.govern.mall.utils.SystemConstants.MAX_PAGE_SIZE;

/**
 * Description:  探店笔记实现类
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-21 19:21
 *
 * @author linhaibo
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private IFollowService followService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultObject<Blog> queryBlogById(Long id) {
        Blog blog = getById(id);
        if (blog == null) {
            return ResultObject.error("笔记不存在！");
        }
        //2.查询blog有关的用户
        queryBlogUser(blog);
        //3.查询blog是否被当前用户点赞
        isBlogLiked(blog);
        return ResultObject.success(blog);
    }

    private void isBlogLiked(Blog blog) {
        String userId = UserHolder.getUser().getId();
        String key = BLOG_LIKE_KEY + blog.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId);
        blog.setIsLike(score != null);
    }

    /**
     * 点赞列表查询
     *
     * @param id 探店笔记id
     * @return top5的点赞用户
     */
    @Override
    public ResultObject<List<UserDTO>> queryBlogLikes(Long id) {
        String key = BLOG_LIKE_KEY + id;
        //1.查询top5的点赞用户 zrange key 0 4
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (top5 == null || top5.isEmpty()) {
            return ResultObject.success(Collections.emptyList());
        }
        //2.解析出用户的id
        List<Long> userIds = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", userIds);
        //3.根据用户id查询用户 WHERE id IN ( 5 , 1 ) ORDER BY FIELD(id, 5, 1)
        List<UserDTO> userList = userService.query().in("id", userIds).last("ORDER BY FIELD(id," + idStr + ")")
                .list()
                .stream().map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        return ResultObject.success(userList);
    }

    /**
     * 探店笔记点赞
     *
     * @param id 笔记id
     */
    @Override
    public ResultObject<String> likeBlog(Long id) {
        //1.获取当前用户
        UserDTO user = UserHolder.getUser();
        //2.判断用户是否点赞
        String key = BLOG_LIKE_KEY + id;
        Double score = stringRedisTemplate.opsForZSet().score(key, user.getId());
        if (score != null) {
            //3.已点赞，则进行取消点赞
            //3.1.数据库点赞-1
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if (isSuccess) {
                //3.2.将用户从set集合移除
                stringRedisTemplate.opsForZSet().remove(key, user.getId());
            }
        } else {
            //4.未点赞，则进行点赞
            //4.1.数据库点赞+1
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if (isSuccess) {
                //4.2.将用户添加到set集合
                stringRedisTemplate.opsForZSet().add(key, user.getId(), System.currentTimeMillis());
            }
        }

        return ResultObject.success("点赞成功！");
    }

    /**
     * 查询热门笔记
     *
     * @param current 当前页码
     * @return
     */
    @Override
    public ResultObject<Page<Blog>> queryHotBlog(Integer current) {
        Page<Blog> page = query().orderByDesc("liked").page(new Page<>(current, MAX_PAGE_SIZE));
        page.getRecords().forEach(blog -> {
            this.queryBlogUser(blog);
            this.isBlogLiked(blog);
        });
        return ResultObject.success(page);
    }

    @Override
    public ResultObject<ScrollResult> queryBlogByFollow(Long max, Integer offset) {
        //1.获取当前用户
        String userId = UserHolder.getUser().getId();
        String key = BLOG_FEED_KEY + userId;
        //2.查询收件箱 ZREVRANGEBYSCORE key Max Min LIMIT offset count
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, MAX_PAGE_SIZE);
        //3.非空判断
        if (typedTuples == null || typedTuples.isEmpty()) {
            return ResultObject.success(new ScrollResult());
        }
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int newOffset = 1;
        //4.解析数据：blogId、minTime（时间戳）、offset
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            //添加blogId
            ids.add(Long.valueOf(Objects.requireNonNull(typedTuple.getValue())));
            //获取时间戳
            long timestamp = typedTuple.getScore().longValue();
            //针对同样的时间戳做处理
            if (timestamp == minTime) {
                newOffset++;
            } else {
                minTime = timestamp;
                newOffset = 1;
            }
        }
        newOffset = minTime == max ? newOffset : newOffset + offset;

        // 5.根据id查询blog
        String idStr = StrUtil.join(",", ids);
        List<Blog> list = query().in("id", ids).last("ORDER BY FIELD(id, " + idStr + ")").list();
        list.forEach(b -> {
            // 5.1.查询blog有关的用户
            queryBlogUser(b);
            // 5.2.查询blog是否被点赞
            isBlogLiked(b);
        });
        // 6.封装并返回
        ScrollResult result = new ScrollResult();
        result.setList(list);
        result.setOffset(newOffset);
        result.setMinTime(minTime);
        return ResultObject.success(result);
    }

    @Override
    public ResultObject<String> saveBlog(Blog blog) {
        //获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(Long.valueOf(user.getId()));
        //2.保存探店笔记
        boolean isSuucess = save(blog);
        if (!isSuucess) {
            return ResultObject.error("新增探店笔记失败!");
        }
        //3.查询笔记作者的所有粉丝 select * from tb_follow where follow_user_id = ?
        List<Follow> followList = followService.query().eq("follow_user_id", user.getId()).list();
        if (followList.size() > 0) {
            for (Follow follow : followList) {
                //4.推送笔记id给所有粉丝
                String key = BLOG_FEED_KEY + follow.getUserId();
                stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
            }
        }
        return ResultObject.success("新增探店笔记成功!");
    }

    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setNickname(user.getNickName());
        blog.setIcon(user.getIcon());
    }
}

