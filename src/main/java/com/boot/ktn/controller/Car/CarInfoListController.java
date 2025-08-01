package com.boot.ktn.controller.Car;

import com.boot.ktn.dto.common.ApiResponseDto;
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
@RequestMapping("${api.base.path}/car")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "기동장비정보 > 기동장비관리", description = "기동장비관리 API")
public class CarInfoListController {
    private static final Logger logger = LoggerFactory.getLogger(CarInfoListController.class);

    private final MapViewProcessor mapViewProcessor;
    private final ResponseEntityUtil responseEntityUtil;
    private final EscapeUtil escapeUtil;
    private final MapViewParamsUtil mapViewParamsUtil;

    @Setter
    @Getter
    String errorMessage;

    @CommonApiResponses
    @PostMapping("/listInfo")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> listInfo(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "CARLISTINFO";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/list unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping("/carCodeList")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> carCodeList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "CARNMLIST";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/list unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

}