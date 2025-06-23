package com.boot.ktn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "api-path-config")
public class ApiPathConfig {
    @Value("${api.base.path:api}")
    private String basePath;

    @PostConstruct
    public void init() {
        // 정규화: 슬래시 제거
        basePath = basePath.trim().replaceAll("^/+|/+$", "");
        if (basePath.isEmpty()) {
            basePath = "api";
        }
    }

    public String getBasePath() {
        return basePath;
    }

    public String getAuthPath() {
        return basePath + "/auth";
    }

    public String getPublicPath() {
        return basePath + "/public";
    }
}