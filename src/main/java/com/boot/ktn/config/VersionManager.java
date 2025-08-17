package com.boot.ktn.config;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class VersionManager {
    private String serverVersion;

    @PostConstruct
    public void init() {
        serverVersion = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }

    public String getServerVersion() {
        return serverVersion;
    }
}