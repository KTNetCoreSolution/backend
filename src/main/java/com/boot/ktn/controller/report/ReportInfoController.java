package com.boot.ktn.controller.report;

import com.boot.ktn.config.AppConfig;
import com.boot.ktn.dto.common.ApiResponseDto;
import com.boot.ktn.entity.mapview.MapViewFileEntity;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping("${api.base.path}/report")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "성과Report", description = "성과Report API")
public class ReportInfoController {
    private static final Logger logger = LoggerFactory.getLogger(ReportInfoController.class);

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
    @PostMapping("/list")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> reportlist(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTLIST";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/reportlist unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping(value = "/reportlistSave")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> reportlistSave(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        String rptCd = "REPORTLISTTRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);
        params.add(empNo);
        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/reportlistSave unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "결과 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping("/infoList")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> reportInfoList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTINFOLIST";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/reportInfoList unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping("/listData")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> listData(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTLISTDATA";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/reportList unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping("/detailInfo")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> detailInfo(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTDETAIL";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/reportList unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping("/reportDataTran")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> reportDataTran(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        String gubun = (String) request.get("gubun");
        String reportId = (String) request.get("reportId");
        String reportNo = (String) request.get("reportNo");
        String title = (String) request.get("title");
        String content = (String) request.get("content");

        if (gubun == null || gubun.trim().isEmpty() || reportId == null || reportId.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "파라미터가 잘못되어 있습니다.");
        }

        if (title == null || title.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "제목과 내용을 입력해주세요.");
        }

        String rptCd = "REPORTTRAN";
        String jobGb = "SET";
        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : "admin";

        List<String> params = Arrays.asList(
                escapeUtil.escape(gubun),
                escapeUtil.escape(reportId),
                escapeUtil.escape(reportNo),
                escapeUtil.escape(title),
                escapeUtil.escape(content),
                escapeUtil.escape(empNo)
        );

        try {
            List<Map<String, Object>> resultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);

            if (resultList.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "REPORT 저장 실패: 결과가 없습니다.");
            }

            Map<String, Object> result = resultList.get(0);
            Long getReportId = Long.parseLong(result.getOrDefault("REPORTID", "-1").toString());
            Long getReportNo = Long.parseLong(result.getOrDefault("REPORTNO", "-1").toString());

            if (getReportId == -1 || getReportNo == -1) {
                throw new IllegalArgumentException("REPORTID, REPORTNO 반환 실패");
            }

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("message", "게시물이 성공적으로 저장되었습니다.");
            responseData.put("reportId", getReportId);
            responseData.put("reportNo", getReportNo);

            return responseEntityUtil.okBodyEntity(responseData);
        } catch (Exception e) {
            errorMessage = "/reportDataTran unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "게시물 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping("/reportFileList")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> reportFileList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTFILES";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/reportFileList unescapedResultList = mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping(value = "/fileSave", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> fileSave(
            String gubun,
            String fileId,
            String reportId,
            String reportNo,
            MultipartFile[] files,
            HttpServletRequest httpRequest) {

        // Validate required parameters
        if (gubun == null || gubun.trim().isEmpty() || reportId == null || reportId.trim().isEmpty() || reportNo == null || reportNo.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "필수파라미터가 잘못되어 있습니다.");
        }

        if (files == null || files.length == 0) {
            return responseEntityUtil.okBodyEntity(new ArrayList<>(), "01", "파일이 필요합니다.");
        }

        if (files.length > fileConfig.getMaxFilesPerUpload()) {
            return responseEntityUtil.okBodyEntity(null, "01", "파일 크기가 " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB 제한을 초과했습니다.");
        }

        String rptCd = "REPORTFILESTRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : "admin";

        List<MapViewFileEntity> result = new ArrayList<>();
        try {
            // Process each file individually
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                if (fileName == null || fileName.trim().isEmpty()) {
                    logger.warn("Skipping file with empty name");
                    continue;
                }

                final Set<String> ALLOWED_EXTENSIONS = Set.of("xlsx", "xls");

                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                String fileSize = String.valueOf(file.getSize());

                List<Object> params = new ArrayList<>();
                params.add(escapeUtil.escape(gubun));
                params.add(escapeUtil.escape(fileId != null ? fileId : ""));
                params.add(escapeUtil.escape(reportId));
                params.add(escapeUtil.escape(reportNo));
                params.add(escapeUtil.escape(empNo));
                params.add(escapeUtil.escape(fileName));
                params.add(escapeUtil.escape(fileType));
                params.add(escapeUtil.escape(fileSize));

                byte[] fileData = file.getBytes();
                params.add(fileData); // LONGBLOB data

                if (!ALLOWED_EXTENSIONS.contains(fileType)) {
                    return responseEntityUtil.okBodyEntity(null, "01",
                            "허용되지 않는 파일 형식입니다. (허용: " + String.join(", ", ALLOWED_EXTENSIONS) + ")");
                }

                if (fileData.length > fileConfig.getMaxFileSize()) {
                    throw new IllegalArgumentException("File size exceeds " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB limit");
                }

                List<MapViewFileEntity> fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);

                result.addAll(fileResult);

                if (result.size() > fileConfig.getMaxResultSize()) {
                    logger.warn("Result size exceeds limit, truncating");
                    break;
                }
            }

            if (result.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "No files were processed successfully.");
            }

            return responseEntityUtil.okBodyEntity(result);
        } catch (IllegalArgumentException e) {
            errorMessage = "/fileSave fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/fileSave fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        }
    }

    @PostMapping(value = "/fileDelete")
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> fileDelete(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        String gubun = (String) request.get("gubun");
        String fileId = (String) request.get("fileId");
        String reportId = (String) request.get("reportId");
        String reportNo = (String) request.get("reportNo");

        // Validate required parameters
        if (gubun == null || gubun.trim().isEmpty() || reportId == null || reportId.trim().isEmpty() || reportNo == null || reportNo.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "필수파라미터가 잘못되어 있습니다.");
        }

        if (!"D".equals(gubun)) {
            return responseEntityUtil.okBodyEntity(null, "01", "Invalid gubun value for deletion. Must be 'D'.");
        }

        String rptCd = "REPORTFILESTRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : "admin";

        List<MapViewFileEntity> result = new ArrayList<>();
        try {
            // Deletion does not require file data, only metadata
            List<Object> params = new ArrayList<>();
            params.add(escapeUtil.escape(gubun));
            params.add(escapeUtil.escape(fileId != null ? fileId : ""));
            params.add(escapeUtil.escape(reportId));
            params.add(escapeUtil.escape(reportNo));
            params.add(escapeUtil.escape(empNo));
            params.add(""); // pFILENM (empty for deletion)
            params.add(""); // pFILETYPE (empty for deletion)
            params.add("0"); // pFILESIZE (0 for deletion)
            params.add(new byte[0]); // pFILEDATA (empty byte array for deletion)

            List<MapViewFileEntity> fileResult = mapViewFileProcessor.processFileDelete(rptCd, params, empNo, jobGb);
            result.addAll(fileResult);

            if (result.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "File deletion failed: No results returned.");
            }

            return responseEntityUtil.okBodyEntity(result, "00", "File deleted successfully.");
        } catch (IllegalArgumentException e) {
            errorMessage = "/fileDelete fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File deletion failed: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/fileDelete fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File deletion failed: " + e.getMessage());
        }
    }
}