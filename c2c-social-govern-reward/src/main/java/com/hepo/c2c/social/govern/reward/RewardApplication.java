package com.hepo.c2c.social.govern.reward;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 发放奖励服务主入口
 *
 * @author linhaibo
 */
@SpringBootApplication
@MapperScan("com.hepo.c2c.social.govern.reward.mapper")
public class RewardApplication {
    public static void main(String[] args) {
        SpringApplication.run(RewardApplication.class, args);
    }
}
