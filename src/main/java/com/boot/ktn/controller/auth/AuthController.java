// AuthController.java
package com.boot.ktn.controller.auth;

import com.boot.ktn.aspect.ClientIPAspect;
import com.boot.ktn.dto.common.ApiResponseDto;
import com.boot.ktn.service.auth.AuthService;
import com.boot.ktn.util.CommonApiResponses;
import com.boot.ktn.util.JwtUtil;
import com.boot.ktn.util.ResponseEntityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.base.path}/auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "1.LOGIN > 인증관리", description = "인증 및 session 관리 API")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final ResponseEntityUtil responseEntityUtil;

    @Setter
    @Getter
    String errorMessage;

    @CommonApiResponses
    @GetMapping("/check")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> check(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtUtil.getTokenFromCookie(request);

        if (token == null) {
            Cookie jwtCookie = new Cookie("jwt_token", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            jwtCookie.setSecure(true);
            jwtCookie.setAttribute("SameSite", jwtUtil.getCookieSameSite());
            response.addCookie(jwtCookie);
            return responseEntityUtil.errBodyEntity("Missing token", 401);
        }

        try {
            Claims claims = jwtUtil.validateToken(token);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("empNo", claims.getSubject());
            responseData.put("empNm", claims.get("empNm"));
            responseData.put("auth", claims.get("auth"));
            responseData.put("orgCd", claims.get("orgCd"));
            responseData.put("orgNm", claims.get("orgNm"));
            responseData.put("levelCd", claims.get("levelCd"));
            responseData.put("expiresAt", claims.getExpiration().getTime() / 1000);
            return responseEntityUtil.okBodyEntity(responseData);
        } catch (Exception e) {
            errorMessage = "Token validation failed in /api/auth/check: ";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            Cookie jwtCookie = new Cookie("jwt_token", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            jwtCookie.setSecure(true);
            jwtCookie.setAttribute("SameSite", jwtUtil.getCookieSameSite());
            response.addCookie(jwtCookie);
            return responseEntityUtil.errBodyEntity(this.getErrorMessage() + e.getMessage(), 401);
        }
    }

    @CommonApiResponses
    @GetMapping("/live")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> live(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "extend", defaultValue = "false") boolean extend) {
        String token = jwtUtil.getTokenFromCookie(request);
        if (token == null) {
            Cookie jwtCookie = new Cookie("jwt_token", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            jwtCookie.setSecure(true);
            jwtCookie.setAttribute("SameSite", jwtUtil.getCookieSameSite());
            response.addCookie(jwtCookie);
            return responseEntityUtil.errBodyEntity("Missing token", 401);
        }

        try {
            Claims claims = authService.validateToken(token);
            String empNo = claims.getSubject();
            String auth = claims.get("auth", String.class);
            String empNm = claims.get("empNm", String.class);
            String orgCd = claims.get("orgCd", String.class);
            String orgNm = claims.get("orgNm", String.class);
            String levelCd = claims.get("levelCd", String.class);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("empNo", empNo);
            userInfo.put("empNm", empNm);
            userInfo.put("auth", auth);
            userInfo.put("orgCd", orgCd);
            userInfo.put("orgNm", orgNm);
            userInfo.put("levelCd", levelCd);
            userInfo.put("ip", ClientIPAspect.getClientIP());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", userInfo);
            long expiresAt = claims.getExpiration().getTime() / 1000;
            responseData.put("expiresAt", expiresAt);

            if (extend) {
                String newToken = jwtUtil.generateToken(empNo, auth, empNm, orgCd, orgNm, levelCd);
                Cookie jwtCookie = jwtUtil.createJwtCookie(newToken);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setSecure(true);
                response.addCookie(jwtCookie);

                Claims newClaims = Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getSigningKey())
                        .build()
                        .parseClaimsJws(newToken)
                        .getBody();
                responseData.put("expiresAt", newClaims.getExpiration().getTime() / 1000);
            }

            return responseEntityUtil.okBodyEntity(responseData);
        } catch (Exception e) {
            errorMessage = "Invalid token: ";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            Cookie jwtCookie = new Cookie("jwt_token", null);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(0);
            jwtCookie.setSecure(true);
            jwtCookie.setAttribute("SameSite", jwtUtil.getCookieSameSite());
            response.addCookie(jwtCookie);
            return responseEntityUtil.errBodyEntity(this.getErrorMessage() + e.getMessage(), 401);
        }
    }

    @CommonApiResponses
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        jwtCookie.setSecure(true);
        jwtCookie.setAttribute("SameSite", jwtUtil.getCookieSameSite());
        response.addCookie(jwtCookie);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("success", true);
        return responseEntityUtil.okBodyEntity(responseData);
    }
}