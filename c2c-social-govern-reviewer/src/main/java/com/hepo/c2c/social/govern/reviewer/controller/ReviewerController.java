package com.hepo.c2c.social.govern.reviewer.controller;

import com.hepo.c2c.social.govern.reviewer.api.service.ReviewerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 评审员控制层
 *
 * @author linhaibo
 */
@RestController
@RequestMapping("/reviewer")
public class ReviewerController {

    @Resource
    private ReviewerService reviewerService;

    @RequestMapping("hello")
    public String sayHello(String name) {
        return reviewerService.testRPC(name);
    }

}
