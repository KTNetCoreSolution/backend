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
                logger.warn(this.getErrorMessage(), e.getMessage(), e);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String authPath = "/" + apiPathConfig.getAuthPath();
        String publicPath = "/" + apiPathConfig.getPublicPath();
        boolean shouldNotFilter = path.equals(authPath + "/login") ||
                path.equals(authPath + "/check") ||
                path.equals(authPath + "/logout") ||
                path.startsWith(publicPath);
        logger.debug("shouldNotFilter: path={} result={}", path, shouldNotFilter);
        return shouldNotFilter;
    }
}