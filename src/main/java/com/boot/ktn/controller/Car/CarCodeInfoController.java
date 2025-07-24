package com.boot.ktn.controller.Car;

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

@RestController
@RequestMapping("${api.base.path}/carcode")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "3.시스템관리 > 차량코드관리", description = "차량코드를 관리하는 API")
public class CarCodeInfoController {
    private static final Logger logger = LoggerFactory.getLogger(CarCodeInfoController.class);

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
    @PostMapping("/codeInfoList")
    public ResponseEntity<ApiResponseDto<List<Map<String, Object>>>> codeInfoList(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest
    ) {
        String rptCd = "CARCODEINFO";
        String jobGb = "GET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : null;

        List<String> params = mapViewParamsUtil.getParams(request, escapeUtil);

        List<Map<String, Object>> unescapedResultList;
        try {
            unescapedResultList = mapViewProcessor.processDynamicView(rptCd, params, empNo, jobGb);
        } catch (IllegalArgumentException e) {
            errorMessage = "/codeInfoList unescapedResultList = mapViewFileProcessor.processDynamicView(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", e.getMessage());
        }

        if (unescapedResultList.isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "조회 결과가 없습니다.");
        }

        return responseEntityUtil.okBodyEntity(unescapedResultList);
    }

    @CommonApiResponses
    @PostMapping(value = "/carCodeInfoUpload", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> carCodeInfoUpload(
            String gubun,
            String CARCD,
            String CARTYPE,
            String CARCLASS,
            String CARSIZE,
            String CARNM,
            MultipartFile[] files,
            HttpServletRequest httpRequest) {

        // Validate required parameters
        if (gubun == null || gubun.trim().isEmpty() || CARTYPE == null || CARTYPE.trim().isEmpty()
                || CARCLASS == null || CARCLASS.trim().isEmpty() || CARSIZE == null || CARSIZE.trim().isEmpty() || CARNM == null || CARNM.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "필수파라미터가 잘못되어 있습니다.");
        }

        if (files == null || files.length == 0) {
            return responseEntityUtil.okBodyEntity(new ArrayList<>(), "01", "파일이 필요합니다.");
        }

        if (files.length > fileConfig.getMaxFilesPerUpload()) {
            return responseEntityUtil.okBodyEntity(null, "01", "파일 크기가 " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB 제한을 초과했습니다.");
        }

        String rptCd = "CARCODEINFOTRAN";
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
                params.add(escapeUtil.escape(CARCD != null ? CARCD : ""));
                params.add(escapeUtil.escape(CARTYPE));
                params.add(escapeUtil.escape(CARCLASS));
                params.add(escapeUtil.escape(CARSIZE));
                params.add(escapeUtil.escape(CARNM));
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
            errorMessage = "/carCodeInfoUpload fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        } catch (Exception e) {
            errorMessage = "/carCodeInfoUpload fileResult = mapViewFileProcessor.processFileUpload(rptCd, params, empNo, jobGb);";
            logger.error(this.getErrorMessage(), e.getMessage(), e);
            return responseEntityUtil.okBodyEntity(null, "01", "File upload failed: " + e.getMessage());
        }
    }

    @CommonApiResponses
    @PostMapping(value = "/carCodeInfoSave")
    public ResponseEntity<ApiResponseDto<List<MapViewFileEntity>>> carCodeInfoSave(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {

        String gubun = (String) request.get("gubun");
        String CARCD = (String) request.get("CARCD");
        String CARTYPE = (String) request.get("CARTYPE");
        String CARCLASS = (String) request.get("CARCLASS");
        String CARSIZE = (String) request.get("CARSIZE");
        String CARNM = (String) request.get("CARNM");

        // Validate required parameters
        if (gubun == null || gubun.trim().isEmpty() || CARCD == null || CARCD.trim().isEmpty() || CARTYPE == null || CARTYPE.trim().isEmpty()
                || CARCLASS == null || CARCLASS.trim().isEmpty() || CARSIZE == null || CARSIZE.trim().isEmpty() || CARNM == null || CARNM.trim().isEmpty()) {
            return responseEntityUtil.okBodyEntity(null, "01", "gubun and fileId are required.");
        }

        String rptCd = "CARCODEINFOTRAN";
        String jobGb = "SET";

        Claims claims = (Claims) httpRequest.getAttribute("user");
        String empNo = claims != null && claims.getSubject() != null ? claims.getSubject() : "admin";

        List<MapViewFileEntity> result = new ArrayList<>();
        try {
            // Deletion does not require file data, only metadata
            List<Object> params = new ArrayList<>();
            params.add(escapeUtil.escape(gubun));
            params.add(escapeUtil.escape(CARCD));
            params.add(escapeUtil.escape(CARTYPE));
            params.add(escapeUtil.escape(CARCLASS));
            params.add(escapeUtil.escape(CARSIZE));
            params.add(escapeUtil.escape(CARNM));
            params.add(escapeUtil.escape(empNo));
            params.add(""); // pIMGNM (empty for deletion)
            params.add(""); // pIMGTYPE (empty for deletion)
            params.add("0"); // pIMGSIZE (0 for deletion)
            params.add(new byte[0]); // pIMGDATA (empty byte array for deletion)

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
}