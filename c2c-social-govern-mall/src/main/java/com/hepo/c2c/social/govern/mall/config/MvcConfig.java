package com.hepo.c2c.social.govern.mall.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hepo.c2c.social.govern.mall.interceptor.LoginInterceptor;
import com.hepo.c2c.social.govern.mall.interceptor.RefreshTokenInterceptor;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.format.Formatter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


/**
 * Spring mvc拦截器配置
 *
 * @author linhaibo
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                        "/user/login",
                        "/user/code"
                ).order(1);

        //添加token刷新拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);

    }



    // 时间格式化
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Bean
    public Formatter<LocalDateTime> localDateFormatter() {

        return new Formatter<LocalDateTime>() {

            @Override
            public LocalDateTime parse(String text, Locale locale) throws ParseException {
                return LocalDateTime.parse(text, FORMATTER);
            }

            @Override
            public String print(LocalDateTime object, Locale locale) {
                return object.format(FORMATTER);
            }
        };
    }
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {

        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder
                // 反序列化
                .deserializerByType(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER))
                // 序列化
                .serializerByType(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));
    }
}
