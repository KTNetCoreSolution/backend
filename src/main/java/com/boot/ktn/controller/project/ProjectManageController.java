package com.boot.ktn.controller.project;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.base.path}/project")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "2.MAIN > 프로젝트관리", description = "프로젝트를 관리하는 API")
public class ProjectManageController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectManageController.class);

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
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> projectList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "PROJECT";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        try {
            List<Map<String, Object>> resultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);

            if (resultList == null || resultList.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
            }
            return responseEntityUtil.okBodyEntity(resultList);
        } catch (IllegalArgumentException e) {
            errorMessage = "/project/list mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        } catch (Exception e) {
            errorMessage = "/project/list unknown error";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", "프로젝트 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping("/detail")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> projectDetail(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "PROJECT";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        try {
            List<Map<String, Object>> resultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);

            if (resultList == null || resultList.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
            }

            Map<String, Object> detail = resultList.get(0);
            return responseEntityUtil.okBodyEntity(detail);

        } catch (IllegalArgumentException e) {
            errorMessage = "/project/detail mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        } catch (Exception e) {
            errorMessage = "/project/detail unknown error";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", "프로젝트 상세 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping("/save")
    public ResponseEntity<ApiResponseDto<Map<String, Object>>> projectSave(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "PROJECTTRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        try {
            List<Map<String, Object>> resultList =
                    mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);

            Map<String, Object> result =
                    (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;

            return responseEntityUtil.okBodyEntity(result);

        } catch (IllegalArgumentException e) {
            errorMessage = "/project/save mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        } catch (Exception e) {
            errorMessage = "/project/save unknown error";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", "프로젝트 저장 중 오류가 발생했습니다.");
        }
    }

    @CommonApiResponses
    @PostMapping("/filelist")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> projectFileList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "PROJECTFILE";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        try {
            List<Map<String, Object>> resultList = mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);

            if (resultList == null || resultList.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
            }

            return responseEntityUtil.okBodyEntity(resultList);
        } catch (IllegalArgumentException e) {
            errorMessage = "/project/filelist mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        } catch (Exception e) {
            errorMessage = "/project/filelist unknown error";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", "프로젝트 파일 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping(value = "/filesave", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> saveFiles(
            String gubun,
            String fileId,
            String projectId,
            MultipartFile[] files,
            HttpServletRequest httpRequest) {

        // Validate required parameters
        if (gubun == null || gubun.trim().isEmpty() || projectId == null || projectId.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "필수파라미터가 잘못되어 있습니다.");
        }

        if (files == null || files.length == 0) {
            return responseEntityUtil.okBodyEntity(new ArrayList<>(), "01", "파일이 필요합니다.");
        }

        if (files.length > fileConfig.getMaxFilesPerUpload()) {
            return responseEntityUtil.okBodyEntity(null, "01", "파일 크기가 " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB 제한을 초과했습니다.");
        }

        String rptCd = "PROJECTFILETRAN";
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
                String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
                String fileSize = String.valueOf(file.getSize());

                List<Object> params = new ArrayList<>();
                params.add(escapeUtil.escape(gubun));
                params.add(escapeUtil.escape(fileId != null ? fileId : ""));
                params.add(escapeUtil.escape(projectId));
                params.add(escapeUtil.escape(empNo));
                params.add(escapeUtil.escape(fileName));
                params.add(escapeUtil.escape(fileType));
                params.add(escapeUtil.escape(fileSize));
                // Stream file content to avoid memory issues
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (InputStream inputStream = file.getInputStream()) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                }
                byte[] fileData = baos.toByteArray();
                if (fileData.length > fileConfig.getMaxFileSize()) {
                    throw new IllegalArgumentException("File size exceeds " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB limit");
                }
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
            errorMessage = "/filesave fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/filesave fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping("/filedelete")
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> deleteProjectFiles(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String gubun = request.get("gubun") != null ? request.get("gubun").toString() : null;
        String fileId = request.get("fileId") != null ? request.get("fileId").toString() : null;
        String projectId = request.get("projectId") != null ? request.get("projectId").toString() : null;

        if (gubun == null || gubun.trim().isEmpty() || projectId == null || projectId.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "gubun과 projectId는 필수입니다.");
        }

        if (!"D".equals(gubun)) {
            return responseEntityUtil.okBodyEntity(null, "01", "삭제 시 gubun은 'D'여야 합니다.");
        }

        String rptCd = "PROJECTFILETRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : "admin";

        try {
            List<Object> params = new ArrayList<>();
            params.add(escapeUtil.escape(gubun));
            params.add(escapeUtil.escape(fileId != null ? fileId : ""));
            params.add(escapeUtil.escape(projectId));
            params.add(escapeUtil.escape(empNo));
            params.add("");
            params.add("");
            params.add("0");
            params.add(new byte[0]);

            List<MapViewFileEntity> result =
                    mapViewFileProcessor.processFileDelete(rptCd, params, empNo, jobGb);

            if (result == null || result.isEmpty()) {
                return responseEntityUtil.okBodyEntity(null, "01", "파일 삭제 실패: 결과가 없습니다.");
            }

            return responseEntityUtil.okBodyEntity(result, "00", "파일이 성공적으로 삭제되었습니다.");

        } catch (IllegalArgumentException e) {
            errorMessage = "/project/filedelete mapViewFileProcessor.processFileDelete(rptCd, params, empNo, jobGb);";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", "파일 삭제 실패: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/project/filedelete unknown error";
            logger.error(errorMessage, e);
            return responseEntityUtil.okBodyEntity(null, "01", "파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}