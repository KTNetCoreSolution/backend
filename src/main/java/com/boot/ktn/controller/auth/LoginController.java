package com.boot.ktn.controller.auth;

import com.boot.ktn.aspect.ClientIPAspect;
import com.boot.ktn.dto.common.ApiResponseDto;
import com.boot.ktn.entity.auth.LoginEntity;
import com.boot.ktn.entity.auth.LoginResultEntity;
import com.boot.ktn.service.auth.LoginResultService;
import com.boot.ktn.service.auth.LoginService;
import com.boot.ktn.util.CommonApiResponses;
import com.boot.ktn.util.JwtUtil;
import com.boot.ktn.util.ResponseEntityUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.base.path}/auth")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "1.LOGIN > 로그인", description = "사용자 로그인 관리 API")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final LoginService loginService;
    private final JwtUtil jwtUtil;
    private final ResponseEntityUtil responseEntityUtil;
    private final LoginResultService loginResultService;
    private final RestTemplate restTemplate;

    @Value("${MKATE_URL:#{''}}")
    private String mKateUrl;

    @Setter
    @Getter
    String errorMessage;

    @CommonApiResponses
    @PostMapping("login")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> login(
            @RequestBody Map<String, String> request,
            HttpServletResponse response,
            HttpSession session) {
        String empNo = request.get("empNo");
        String empPwd = request.get("empPwd");
        String captchaInput = request.get("captchaInput");

        if (empNo == null || empPwd == null || captchaInput == null) {
            return responseEntityUtil.okBodyEntity(null, "01", "아이디, 비밀번호, 캡챠는 필수입니다.");
        }

        // Verify CAPTCHA
        String captchaText = (String) session.getAttribute("captchaText");
        if (!captchaInput.equalsIgnoreCase(captchaText)) {
            return responseEntityUtil.okBodyEntity(null, "01", "캡챠가 일치하지 않습니다.");
        }

        try {
            LoginEntity loginEntity = loginService.loginCheck(empNo, empPwd);

            if (loginEntity != null) {
                return processLoginSuccess(loginEntity, response, empNo);
            } else {
                return responseEntityUtil.okBodyEntity(null, "01", "아이디 또는 비밀번호가 잘못되었습니다.");
            }
        } catch (Exception e) {
            errorMessage = "로그인 처리 중 오류 발생: ";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            System.out.println(this.getErrorMessage() + e.getMessage());
            return responseEntityUtil.errBodyEntity(this.getErrorMessage() + e.getMessage(), 500);
        }
    }

    @CommonApiResponses
    @PostMapping("sso/login/check")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> ssoLoginCheck(
            @NotNull @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse response,
            HttpSession session) {

        logger.debug("request: {}", request);

        String empNo = request.get("empNo");
        String ssoCheck = request.get("ssoCheck");

        logger.debug("empNo: {}", empNo);
        logger.debug("ssoCheck: {}", ssoCheck);

        if (empNo == null || empNo.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "empNo 파라미터 값이 유효하지 않습니다.");
        }
        // test 파라미터 검증
        if (ssoCheck == null || !ssoCheck.matches("^[YN]$")) {
            logger.error("유효하지 않은 ssoCheck 값: {}", ssoCheck);
            return responseEntityUtil.okBodyEntity(null, "01", "ssoCheck 파라미터 값이 유효하지 않습니다.");
        }

        try {
            LoginEntity loginEntity = loginService.ssoLoginCheck(empNo);

            if (loginEntity != null) {
                return processLoginSuccess(loginEntity, response, empNo);
            } else {
                return responseEntityUtil.okBodyEntity(null, "01", "아이디 또는 비밀번호가 잘못되었습니다.");
            }
        } catch (Exception e) {
            errorMessage = "로그인 처리 중 오류 발생: ";
            logger.error(errorMessage, e.getMessage(), e);
            System.out.println(errorMessage + e.getMessage());
            return responseEntityUtil.errBodyEntity(errorMessage + e.getMessage(), 500);
        }
    }

    @CommonApiResponses
    @PostMapping("sso/login")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> ssoLogin(
            @NotNull @RequestBody Map<String, String> request,
            HttpServletRequest httpRequest,
            HttpServletResponse response,
            HttpSession session) {

        logger.debug("request: {}", request);

        String ssoToken = request.get("token");
        String test = request.get("test");
        logger.debug("ssoToken: {}", ssoToken);
        logger.debug("test: {}", test);

        if (ssoToken == null || ssoToken.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "SSO 토큰은 필수입니다.");
        }

        // test 파라미터 검증
        if (test == null || !test.matches("^[YN]$")) {
            logger.error("유효하지 않은 test 값: {}", test);
            return responseEntityUtil.okBodyEntity(null, "01", "test 파라미터 값이 유효하지 않습니다.");
        }

        // ssoToken을 URL 인코딩
        /*
        String encodedSsoToken;
        try {
            encodedSsoToken = URLEncoder.encode(ssoToken, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("토큰 인코딩 실패: {}", e.getMessage());
            return responseEntityUtil.errBodyEntity("토큰 인코딩 실패: " + e.getMessage(), 500);
        }
        */

        URI getUrl = UriComponentsBuilder.fromUriString(mKateUrl)
                .queryParam("token", ssoToken)
                .encode()
                .build(true)
                .toUri();

        /*
        URI getUrl = UriComponentsBuilder.fromUriString(mKateUrl)
                .queryParam("token", ssoToken)
                .build(true)
                .toUri();
        */

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
                return responseEntityUtil.errBodyEntity("m-kate 응답이 없습니다.", 500);
            }
        } catch (Exception e) {
            logger.error("HTTP 요청 중 오류 발생: {}", e.getMessage());
            return responseEntityUtil.errBodyEntity("m-kate 요청 실패: " + e.getMessage(), 500);
        }

        try {
            // m-kate 응답 파싱 (안전한 캐스팅)
            Object resultObj = mKateResponse.get("result");
            if (!(resultObj instanceof Map)) {
                logger.error("m-kate 응답 형식이 올바르지 않습니다.: resultObj={}", resultObj);
                return responseEntityUtil.errBodyEntity("m-kate 응답 형식이 올바르지 않습니다.", 500);
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
                logger.error("m-kate 인증 실패: code={}, errdesc={}", code, errdesc);
                return responseEntityUtil.errBodyEntity(errdesc, 401);
            }

            if (empNo == null || empNo.isEmpty() || "null".equals(empNo)) {
                return responseEntityUtil.okBodyEntity(null, "01", "아이디는 필수입니다.");
            }

            LoginEntity loginEntity = loginService.ssoLoginCheck(empNo);

            if (loginEntity != null) {
                return processLoginSuccess(loginEntity, response, empNo);
            } else {
                return responseEntityUtil.okBodyEntity(null, "01", "아이디 또는 비밀번호가 잘못되었습니다.");
            }
        } catch (Exception e) {
            errorMessage = "로그인 처리 중 오류 발생: ";
            logger.error(errorMessage, e.getMessage(), e);
            System.out.println(errorMessage + e.getMessage());
            return responseEntityUtil.errBodyEntity(errorMessage + e.getMessage(), 500);
        }
    }

    /**
     * 로그인 성공 시 공통 로직을 처리하는 메서드
     */
    private ResponseEntity<ApiResponseDto<Map<String, Object>>> processLoginSuccess(
            LoginEntity loginEntity,
            HttpServletResponse response,
            String empNo) {
        String clientIP = ClientIPAspect.getClientIP();
        loginEntity.setClientIP(clientIP);
        String token = jwtUtil.generateToken(empNo, loginEntity.getAuth(), loginEntity.getEmpNm(), loginEntity.getOrgCd(), loginEntity.getOrgNm());
        Claims claims = jwtUtil.validateToken(token);

        // Set HTTP-only cookie
        Cookie jwtCookie = jwtUtil.createJwtCookie(token);
        response.addCookie(jwtCookie);

        // loginResultService call
        Map<String, Object> procedureResult = loginResultService.callLoginProcedure(empNo, clientIP);
        LoginResultEntity loginResult = new LoginResultEntity();
        loginResult.setErrCd((String) procedureResult.get("errCd"));
        loginResult.setErrMsg((String) procedureResult.get("errMsg"));

        if (!"00".equals(loginResult.getErrCd())) {
            return responseEntityUtil.okBodyEntity(null, "01", "로그인 프로시저 처리 실패: " + loginResult.getErrMsg());
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("empNo", loginEntity.getEmpNo());
        userInfo.put("empNm", loginEntity.getEmpNm());
        userInfo.put("auth", loginEntity.getAuth());
        userInfo.put("levelCd", loginEntity.getLevelCd());
        userInfo.put("orgCd", loginEntity.getOrgCd());
        userInfo.put("orgNm", loginEntity.getOrgNm());
        userInfo.put("carOrgCd", loginEntity.getCarOrgCd());
        userInfo.put("carOrgNm", loginEntity.getCarOrgNm());
        userInfo.put("carMngOrgCd", loginEntity.getCarMngOrgCd());
        userInfo.put("carMngOrgNm", loginEntity.getCarMngOrgNm());
        userInfo.put("standardSectionCd", loginEntity.getStandardSectionCd());
        userInfo.put("standardSectionNm", loginEntity.getStandardSectionNm());
        userInfo.put("pwdChgYn", loginEntity.getPwdChgYn());
        userInfo.put("ip", clientIP);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("user", userInfo);
        responseData.put("expiresAt", claims.getExpiration().getTime() / 1000);

        return responseEntityUtil.okBodyEntity(responseData);
    }
}