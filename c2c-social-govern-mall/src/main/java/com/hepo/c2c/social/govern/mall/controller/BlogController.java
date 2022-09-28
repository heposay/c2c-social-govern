package com.hepo.c2c.social.govern.mall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hepo.c2c.social.govern.mall.domain.Blog;
import com.hepo.c2c.social.govern.mall.dto.ScrollResult;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.mall.service.IBlogService;
import com.hepo.c2c.social.govern.mall.utils.UserHolder;
import com.hepo.c2c.social.govern.vo.ResultObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.List;

import static com.hepo.c2c.social.govern.mall.utils.SystemConstants.MAX_PAGE_SIZE;


/**
 * (Blog)表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Resource
    private IBlogService blogService;

    @PostMapping("/add")
    public ResultObject<String> addBlog(@RequestBody Blog blog) {
        //获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(Long.valueOf(user.getId()));
        blogService.save(blog);
        return ResultObject.success("保存笔记成功！");
    }

    /**
     * 用户点赞
     *
     * @param id
     * @return
     */
    @PostMapping("/like/{id}")
    public ResultObject<String> likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    @GetMapping("/{id}")
    public ResultObject<Blog> queryBlogById(@PathVariable("id") Long id) {
        return blogService.queryBlogById(id);
    }

    @GetMapping("/of/me")
    public ResultObject<Page<Blog>> queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        UserDTO user = UserHolder.getUser();
        Page<Blog> page = blogService.query().eq("user_id", user.getId()).page(new Page<>(current, MAX_PAGE_SIZE));
        return ResultObject.success(page);
    }

    @GetMapping("/hot")
    public ResultObject<Page<Blog>> queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.queryHotBlog(current);
    }


    @PostMapping("/likes/{id}")
    public ResultObject<List<UserDTO>> queryBlogLikes(@PathVariable("id") Long id) {
        return blogService.queryBlogLikes(id);
    }

    @GetMapping("/of/user")
    public ResultObject<Page<Blog>> queryBlogByUserId(@RequestParam(value = "current", defaultValue = "1") Integer current, @RequestParam(value = "euserId") Long userId) {
        Page<Blog> page = blogService.query().eq("user_id", userId).page(new Page<>(current, MAX_PAGE_SIZE));
        return ResultObject.success(page);
    }

    @GetMapping("/of/follow")
    public ResultObject<ScrollResult> queryBlogByFollow(@RequestParam(value = "lastId") Long max, @RequestParam(value = "offset") Integer offset) {
        return blogService.queryBlogByFollow(max, offset);
    }

}
