package com.boot.ktn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //캐시 완전 무효화 할때만 사용
//        registry.addResourceHandler("/static/**")
//                .addResourceLocations("classpath:/static/")
//                .setCacheControl(CacheControl.noCache().noStore().mustRevalidate());
    }
}
