package com.boot.ktn.controller.Car;

import com.boot.ktn.config.AppConfig;
import com.boot.ktn.dto.common.ApiResponseDto;
import com.boot.ktn.service.mapview.MapViewFileProcessor;
import com.boot.ktn.service.mapview.MapViewProcessor;
import com.boot.ktn.util.CommonApiResponses;
import com.boot.ktn.util.EscapeUtil;
import com.boot.ktn.util.MapViewParamsUtil;
import com.boot.ktn.util.ResponseEntityUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.base.path}/carStat")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "모바일 > 운행일지", description = "운행일지를 관리하는 API")
public class CarStatController {
    private static final Logger logger = LoggerFactory.getLogger(CarStatController.class);

    private final ResponseEntityUtil responseEntityUtil;
    private final MapViewProcessor mapViewProcessor;
    private final MapViewFileProcessor mapViewFileProcessor;
    private final EscapeUtil escapeUtil;
    private final MapViewParamsUtil mapViewParamsUtil;
    private final AppConfig.FileConfig fileConfig;

    @Setter
    @Getter
    String errorMessage;

    @CommonApiResponses
    @PostMapping("/workPerformance")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> userCarList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "WORKPERFORMANCEINFO";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/userCarList unescapedResultList = mapViewParamsUtil.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }
}