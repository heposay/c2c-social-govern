package com.hepo.c2c.social.govern.reviewer;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 评审员服务主入口
 *
 * @author linhaibo
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ReviewerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewerApplication.class, args);
    }
}
