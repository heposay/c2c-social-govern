package com.hepo.c2c.social.govern.report;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 主启动类入口
 *
 * @author linhaibo
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.hepo.c2c.social.govern.report.mapper")
public class ReportApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
