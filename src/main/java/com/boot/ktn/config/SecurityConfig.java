package com.boot.ktn.config;

import com.boot.ktn.entity.auth.LoginEntity;
import com.boot.ktn.mapper.auth.LoginMapper;
import com.boot.ktn.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final ApiPathConfig apiPathConfig;
    private final Environment environment;
    private final LoginMapper loginMapper;

    @Autowired
    public SecurityConfig(ApiPathConfig apiPathConfig, Environment environment, LoginMapper loginMapper) {
        this.apiPathConfig = apiPathConfig;
        this.environment = environment;
        this.loginMapper = loginMapper;
    }

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        boolean isRender = isRenderEnvironment();
        if (isRender) {
            http.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        }

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 적용
                // JWT 인증 사용으로 CSRF는 비활성화
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // OPTIONS 요청(프리플라이트 요청)은 인증 없이 허용 (CORS 헤더 대응)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Swagger 및 OpenAPI 경로는 인증 없이 접근 가능
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // 공용(public) 경로는 인증 없이 허용
                        .requestMatchers("/" + apiPathConfig.getPublicPath() + "/**").permitAll()

                        // 인증(auth) 경로 중 로그인(/login), 로그아웃(/logout), 상태 확인(/check)은 허용
                        .requestMatchers("/" + apiPathConfig.getAuthPath() + "/login",
                                "/" + apiPathConfig.getAuthPath() + "/logout",
                                "/" + apiPathConfig.getAuthPath() + "/check",
                                "/" + apiPathConfig.getAuthPath() + "/password/**",
                                "/" + apiPathConfig.getAuthPath() + "/captcha").permitAll()
                        .requestMatchers("/", "/index.html", "/assets/**", "/mobile", "/mobile/**", "/404.html").permitAll()

                        // 그 외 기본 경로는 인증 필요
                        .requestMatchers("/" + apiPathConfig.getBasePath() + "/**").authenticated()

                        // 기타 요청은 인증 필요
                        .anyRequest().authenticated()
                )

                // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                // 인증 실패 시 응답 처리
                .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
                    // 인증 실패 응답 설정
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setContentType("application/json");
                    res.getWriter().write("{\"success\": false, \"message\": \"Unauthorized\"}");
                }));

        return http.build();
    }

    private boolean isRenderEnvironment() {
        String env = environment.getProperty("PORT");
        return env != null && !env.isEmpty();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = getCorsConfiguration();
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/" + apiPathConfig.getBasePath() + "/**", configuration);
        source.registerCorsConfiguration("/v3/api-docs/**", configuration);
        source.registerCorsConfiguration("/swagger-ui/**", configuration);
        return source;
    }

    @NotNull
    private CorsConfiguration getCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();

        String[] originsArray = allowedOrigins.split(",");
        System.out.println("allowedOrigins: " + String.join(", ", originsArray));

        for (String origin : originsArray) {
            String trimmedOrigin = origin.trim();
            if (!trimmedOrigin.isEmpty()) {
                configuration.addAllowedOrigin(trimmedOrigin);
            }
        }

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization")); // Swagger에서 authorization 읽기 가능하도록
        configuration.setAllowCredentials(true); // JWT 같은 인증 데이터를 허용

        return configuration;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            LoginEntity user = loginMapper.loginCheck(username, null); // Password not needed for JWT
            if (user == null) {
                throw new UsernameNotFoundException("User not found: " + username);
            }
            return new User(
                    user.getEmpNo(),
                    user.getEmpPwd(),
                    Collections.singletonList(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getAuth()))
            );
        };
    }
}