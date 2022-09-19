package com.hepo.c2c.social.govern.mall.controller;

import com.hepo.c2c.social.govern.mall.domain.Blog;
import com.hepo.c2c.social.govern.mall.service.IBlogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Blog表控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/blog")
public class BlogController {
    /**
     * 服务对象
     */
    @Resource
    private IBlogService blogService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("selectOne")
    public Blog selectOne(Integer id) {
        return blogService.getById(id);
    }

}
