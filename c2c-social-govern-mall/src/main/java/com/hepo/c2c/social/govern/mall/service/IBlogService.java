package com.hepo.c2c.social.govern.mall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hepo.c2c.social.govern.mall.domain.Blog;
import com.hepo.c2c.social.govern.mall.dto.ScrollResult;
import com.hepo.c2c.social.govern.mall.dto.UserDTO;
import com.hepo.c2c.social.govern.vo.ResultObject;

import java.util.List;

/**
 * Blog Service层接口
 *
 * @author linhaibo
 */
public interface IBlogService extends IService<Blog> {


    ResultObject<Blog> queryBlogById(Long id);

    ResultObject<List<UserDTO>> queryBlogLikes(Long id);

    ResultObject<String> likeBlog(Long id);

    ResultObject<Page<Blog>> queryHotBlog(Integer current);

    ResultObject<ScrollResult> queryBlogByFollow(Long max, Integer offset);

    ResultObject<String> saveBlog(Blog blog);
}
