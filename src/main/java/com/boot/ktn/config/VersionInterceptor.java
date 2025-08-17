package com.boot.ktn.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class VersionInterceptor implements HandlerInterceptor {

    private static final String LOCAL_VERSION = "local-dev"; // 로컬 고정값

    @Autowired
    private VersionManager versionManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        response.setHeader("X-Server-Version", versionManager.getServerVersion());

        String clientVersion = request.getHeader("X-Client-Version");
        if (clientVersion == null) {
            response.setStatus(418); // 클라이언트 버전 없으면 강제 갱신
            return false;
        }

        // 로컬 개발: 고정값 "local-dev"면 체크 생략
        if (LOCAL_VERSION.equals(clientVersion)) {
            return true; // 정상 진행
        }

        // 동적 버전 비교
        if (!clientVersion.equals(versionManager.getServerVersion())) {
            response.setStatus(418);
            return false;
        }
        return true;
    }
}