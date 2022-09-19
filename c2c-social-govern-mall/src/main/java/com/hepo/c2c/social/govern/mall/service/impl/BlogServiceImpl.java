package com.hepo.c2c.social.govern.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hepo.c2c.social.govern.mall.domain.Blog;
import com.hepo.c2c.social.govern.mall.mapper.BlogMapper;
import com.hepo.c2c.social.govern.mall.service.IBlogService;
import org.springframework.stereotype.Service;
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
}
