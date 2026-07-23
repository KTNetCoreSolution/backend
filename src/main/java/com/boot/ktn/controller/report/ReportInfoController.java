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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    @PostMapping("/dataList")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> dataList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTDATALIST";
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
    @PostMapping(value = "/reportUpload", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> reportUpload(
            String pGUBUN,
            String pREPORTID,
            String pGBN,
            String pTITLE,
            MultipartFile[] files,
            HttpServletRequest httpRequest) {

        // Validate required parameters
        if (pGUBUN == null || pGUBUN.trim().isEmpty() || pREPORTID == null || pREPORTID.trim().isEmpty()
                 || pGBN == null || pGBN.trim().isEmpty() || pTITLE == null || pTITLE.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "필수파라미터가 잘못되어 있습니다.");
        }

        if (files == null || files.length == 0) {
            return responseEntityUtil.okBodyEntity(new ArrayList<>(), "01", "파일이 필요합니다.");
        }

        if (files.length > fileConfig.getMaxFilesPerUpload()) {
            return responseEntityUtil.okBodyEntity(null, "01", "파일 크기가 " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB 제한을 초과했습니다.");
        }

        final Set<String> ALLOWED_EXTENSIONS = Set.of("xlsx", "xls");

        String rptCd = "REPORTINFOTRAN";
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

                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (!ALLOWED_EXTENSIONS.contains(fileType)) {
                    return responseEntityUtil.okBodyEntity(null, "01",
                            "허용되지 않는 파일 형식입니다. (허용: " + String.join(", ", ALLOWED_EXTENSIONS) + ")");
                }

                if (file.getSize() > fileConfig.getMaxFileSize()) {
                    return responseEntityUtil.okBodyEntity(null, "01",
                            "파일 크기가 제한을 초과했습니다. (최대 " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB)");
                }

                String fileSize = String.valueOf(file.getSize());

                List<Object> params = new ArrayList<>();
                params.add(escapeUtil.escape(pGUBUN));
                params.add(escapeUtil.escape(pREPORTID));
                params.add("");
                params.add(escapeUtil.escape(pGBN));
                params.add(escapeUtil.escape(pTITLE));
                params.add(escapeUtil.escape(empNo));
                params.add(escapeUtil.escape(fileName));
                params.add(escapeUtil.escape(fileType));
                params.add(escapeUtil.escape(fileSize));
                byte[] fileData = file.getBytes();
                params.add(fileData); // LONGBLOB data
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
            errorMessage = "/reportUpload fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/reportUpload fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping(value = "/reportInfoSave")
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> reportInfoSave(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        String pGUBUN = (String) request.get("pGUBUN");
        String pREPORTID = (String) request.get("pREPORTID");
        String pREPORTNO = (String) request.get("pREPORTNO");
        String pGBN = (String) request.get("pGBN");
        String pTITLE = (String) request.get("pTITLE");

        // Validate required parameters
        if (pGUBUN == null || pGUBUN.trim().isEmpty() || pREPORTID == null || pREPORTID.trim().isEmpty() || pREPORTNO == null || pREPORTNO.trim().isEmpty()
                || pGBN == null || pGBN.trim().isEmpty() || pTITLE == null || pTITLE.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "필수파라미터가 잘못되어 있습니다.");
        }

        String rptCd = "REPORTINFOTRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : "admin";

        List<MapViewFileEntity> result = new ArrayList<>();
        try {
            // Deletion does not require file data, only metadata
            List<Object> params = new ArrayList<>();
            params.add(escapeUtil.escape(pGUBUN));
            params.add(escapeUtil.escape(pREPORTID));
            params.add(escapeUtil.escape(pREPORTNO));
            params.add(escapeUtil.escape(pGBN));
            params.add(escapeUtil.escape(pTITLE));
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
            errorMessage = "/carCodeInfoSave fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File deletion failed: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/carCodeInfoSave fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File deletion failed: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping("/filedata")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> filedata(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "REPORTFILEDATA";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/filedata unescapedResultList = mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }
}