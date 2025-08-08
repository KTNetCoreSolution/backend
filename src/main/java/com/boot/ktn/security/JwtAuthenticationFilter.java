package com.boot.ktn.security;

import com.boot.ktn.config.ApiPathConfig;
import com.boot.ktn.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private ApiPathConfig apiPathConfig;

    @Autowired
    private JwtUtil jwtUtil;

    @Setter
    @Getter
    String errorMessage;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws java.io.IOException, jakarta.servlet.ServletException {
        String token = jwtUtil.getTokenFromCookie(request);
        if (token != null) {
            try {
                Claims claims = jwtUtil.validateToken(token);
                request.setAttribute("user", claims);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                errorMessage = "JWT validation failed: {}";
                logger.warn(errorMessage, e.getMessage(), e);
                Cookie jwtCookie = new Cookie("jwt_token", null);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge(0);
                jwtCookie.setSecure(jwtUtil.getCookieSecure());
                jwtCookie.setAttribute("SameSite", jwtUtil.getCookieSameSite());
                response.addCookie(jwtCookie);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String authPath = "/" + (apiPathConfig.getAuthPath() != null ? apiPathConfig.getAuthPath() : "auth");
        String publicPath = "/" + (apiPathConfig.getPublicPath() != null ? apiPathConfig.getPublicPath() : "public");

        // 필터링 제외 경로: 인증 및 공개 경로 처리
        boolean shouldNotFilter = path.equals(authPath + "/login") || // 로그인
                path.startsWith(authPath + "/login/") || // /login/ 하위 경로
                path.equals(authPath + "/check") ||  // 인증 상태 확인
                path.equals(authPath + "/logout") || // 로그아웃
                path.startsWith(publicPath);  // 공용 경로(public)

        logger.debug("Filter Decision - method: {}, path: {}, shouldNotFilter: {}",
                request.getMethod(), path, shouldNotFilter);

        return shouldNotFilter;
    }
}