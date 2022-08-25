package com.hepo.c2c.social.govern.reviewer;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 评审员服务主入口
 *
 * @author linhaibo
 */
@SpringBootApplication
@MapperScan(basePackages = "com.hepo.c2c.social.govern.reviewer.mapper")
public class ReviewerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReviewerApplication.class, args);
    }
}
