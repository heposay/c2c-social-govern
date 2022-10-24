package com.hepo.c2c.social.govern.mall.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hepo.c2c.social.govern.mall.domain.Item;
import com.hepo.c2c.social.govern.mall.domain.ItemStock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 * Project:  c2c-social-govern
 * CreateDate: Created in 2022-09-29 16:34
 *
 * @author linhaibo
 */
@Configuration
public class CaffeineConfig {

    @Bean
    public Cache<Long, Item> itemCache() {
        return Caffeine.newBuilder().initialCapacity(100).maximumSize(1000).build();
    }

    @Bean
    public Cache<Long, ItemStock> stockCache() {
        return Caffeine.newBuilder().initialCapacity(100).maximumSize(1000).build();
    }

}
