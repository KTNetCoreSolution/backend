package com.boot.ktn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.CacheControl;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 자원에 대한 캐시 정책
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(365, TimeUnit.DAYS).cachePublic().immutable());

        // index.html 또는 핵심 페이지는 무효화 정책 (짧은 시간)
        registry.addResourceHandler("/index.html")
                .addResourceLocations("classpath:/static/index.html")
                .setCacheControl(CacheControl.noStore());
    }
}
