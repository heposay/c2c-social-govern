package com.hepo.c2c.social.govern.reward;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 发放奖励服务主入口
 *
 * @author linhaibo
 */
@SpringBootApplication
@EnableDiscoveryClient
public class RewardApplication {
    public static void main(String[] args) {
        SpringApplication.run(RewardApplication.class, args);
    }
}
