package com.boot.ktn.controller.sso;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;

@RestController
@RequestMapping("mobile")
@io.swagger.v3.oas.annotations.tags.Tag(name = "1.SSO > mkate 로그인", description = "SSO mkate 로그인 관리 API")
public class SSoMKateController {
    private static final Logger logger = LoggerFactory.getLogger(SSoMKateController.class);

    private final RestTemplate restTemplate;

    @Value("${MKATE_URL:#{''}}")
    private String mKateUrl;

    public SSoMKateController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // 공통 응답 생성 메서드
    private ResponseEntity<Map<String, Object>> createResponse(boolean success, Map<String, Object> data, String code, String message, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        Map<String, Object> body = new HashMap<>();
        body.put("success", success);
        body.put("data", data);
        body.put("code", code);
        body.put("message", message);
        return new ResponseEntity<>(body, headers, status);
    }

    @GetMapping("ssoMLoginTest")
    public ResponseEntity<Map<String, Object>> ssoLogin(
            @NotNull @RequestParam Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse response,
            HttpSession session) {

        logger.debug("request: {}", request);

        // .NET 참조: 쿼리 파라미터로 데이터 수신
        String ssoToken = request.getOrDefault("token", httpRequest.getParameter("token"));
        String corpFlag = request.getOrDefault("corpFlag", httpRequest.getParameter("corpFlag"));

        if (corpFlag == null || corpFlag.isEmpty()) {
            return createResponse(true, null, "01", "corpFlag 파라미터 필수", HttpStatus.BAD_REQUEST);
        }

        // 토큰 검증: ssoToken 없으면 sso_token fallback
        if (ssoToken == null || ssoToken.isEmpty()) {
            return createResponse(true, null, "01", "SSO 토큰 필수", HttpStatus.BAD_REQUEST);
        }

        String empNoTest = "admin";

        if (empNoTest != null) {
            //로컬 테스트
            //String redirectUrl = "http://localhost:5173/ktn/mobile/ssoMLoginCheck?empNo=" + URLEncoder.encode(empNoTest, StandardCharsets.UTF_8) + "&ssoCheck=Y";
            String redirectUrl = "/mobile/ssoMLoginCheck?empNo=" + URLEncoder.encode(empNoTest, StandardCharsets.UTF_8) + "&ssoCheck=Y";
            try {
                response.sendRedirect(redirectUrl);
            }
            catch (Exception ex)
            {
                return null; // 리다이렉트 후 null 반환
            }
            return null; // 리다이렉트 후 null 반환
        }


        // .NET 소스 참고: ssoToken 인코딩 제거 및 build(true) 제거
        URI getUrl = UriComponentsBuilder.fromUriString(mKateUrl)
                .queryParam("token", ssoToken) // 인코딩 없이 직접 추가
                .build() // .NET과 유사하게 encode() 및 build(true) 제거
                .toUri();

        logger.debug("getUrl: {}", getUrl);

        // HttpClient 생성 후 timeout 설정
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(3000);
        RestTemplate restTemplate = new RestTemplate(requestFactory);

        Map<String, Object> mKateResponse = null;
        try {
            mKateResponse = restTemplate.exchange(
                    getUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();

            if (mKateResponse == null) {
                return createResponse(false, null, "01", "mkate 응답이 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            logger.error("HTTP 요청 중 오류 발생: {}", e.getMessage());
            return createResponse(false, null, "01", "mkate 요청 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            // .NET 참조: JSON 파싱 안전하게
            Object resultObj = mKateResponse.get("result");
            if (!(resultObj instanceof Map)) {
                logger.error("mkate 응답 형식이 올바르지 않습니다.: resultObj={}", resultObj);
                return createResponse(false, null, "01", "mkate 응답 형식이 올바르지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) resultObj;

            String code = String.valueOf(result.get("code"));
            String errdesc = String.valueOf(result.get("errdesc"));
            String empNo = String.valueOf(mKateResponse.get("userid"));

            logger.debug("code: {}", code);
            logger.debug("errdesc: {}", errdesc);
            logger.debug("empNo: {}", empNo);

            if (!"0".equals(code)) {
                logger.error("mkate 인증 실패: code={}, errdesc={}", code, errdesc);
                return createResponse(false, null, "01", errdesc, HttpStatus.UNAUTHORIZED);
            }

            if (empNo == null || empNo.isEmpty() || "null".equals(empNo)) {
                return createResponse(true, null, "01", "아이디는 필수입니다.", HttpStatus.BAD_REQUEST);
            }

            // empNo 검증 통과 시 리다이렉트
            String redirectUrl = "/mobile/ssoMLoginCheck?empNo=" + URLEncoder.encode(empNo, StandardCharsets.UTF_8) + "&ssoCheck=Y";
            response.sendRedirect(redirectUrl);
            return null; // 리다이렉트 후 null 반환

        } catch (Exception e) {
            logger.error("로그인 처리 중 오류 발생: {}", e.getMessage());
            return createResponse(false, null, "01", "로그인 처리 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}