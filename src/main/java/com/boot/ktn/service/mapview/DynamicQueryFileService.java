package com.boot.ktn.service.mapview;

import com.boot.ktn.config.AppConfig;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DynamicQueryFileService {

    private static final Logger logger = LoggerFactory.getLogger(DynamicQueryFileService.class);

    private final DataSource dataSource;
    private final AppConfig.FileConfig fileConfig;

    public List<Map<String, Object>> executeDynamicFileQuery(String procedureCall, List<Object> originalParams) {
        List<Map<String, Object>> mappedResult = new ArrayList<>();
        Connection connection = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            String procedureName = procedureCall.substring(0, procedureCall.indexOf('(')).trim();
            String paramString = procedureTAB("(", ")", procedureCall).trim();
            List<Object> params = parseParameters(paramString, originalParams);
            // Modified: Construct EXEC statement without curly braces for MSSQL
            String placeholders = String.join(",", Collections.nCopies(params.size(), "?"));
            String callString = "EXEC " + procedureName + " " + placeholders;

            connection = DataSourceUtils.getConnection(dataSource);
            if (connection == null) {
                String errorMsg = "executeDynamicFileQuery failed: Unable to obtain JDBC Connection from DataSource";
                logger.error(errorMsg);
                throw new IllegalStateException("Unable to obtain JDBC Connection from DataSource");
            }

            // Modified: Use plain EXEC statement without {} wrapping
            stmt = connection.prepareCall(callString);

            // Modified: Set parameters, handling byte[] and strings appropriately
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    String value = ((String) param).replace("''", "'"); // Undo double-quote escaping
                    stmt.setString(i + 1, value);
                } else if (param instanceof byte[]) {
                    byte[] data = (byte[]) param;
                    if (data.length > fileConfig.getMaxFileSize()) {
                        String errorMsg = "executeDynamicFileQuery failed: File size exceeds " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB limit for procedureCall: " + procedureCall;
                        logger.error(errorMsg);
                        throw new IllegalArgumentException("File size exceeds " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB limit");
                    }
                    stmt.setBinaryStream(i + 1, new ByteArrayInputStream(data), data.length);
                } else {
                    stmt.setObject(i + 1, param);
                }
            }

            boolean hasResultSet = stmt.execute();
            if (!hasResultSet) {
                return Collections.emptyList();
            }

            rs = stmt.getResultSet();
            if (rs == null) {
                return Collections.emptyList();
            }

            List<String> columnNames = getColumnNames(rs);

            while (rs.next()) {
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < columnNames.size(); i++) {
                    Object value = rs.getObject(i + 1);
                    if ("FILEDATA".equalsIgnoreCase(columnNames.get(i)) && value instanceof Blob) {
                        Blob blob = (Blob) value;
                        rowMap.put(columnNames.get(i), blob.getBinaryStream());
                    } else {
                        rowMap.put(columnNames.get(i), value == null ? "" : value);
                    }
                }
                mappedResult.add(rowMap);
                if (mappedResult.size() > fileConfig.getMaxResultSize()) {
                    logger.warn("Result size exceeds limit: {}", mappedResult.size());
                    break;
                }
            }

        } catch (Exception e) {
            String errorMsg = "executeDynamicFileQuery failed for procedureCall: " + procedureCall;
            logger.error(errorMsg, e.getMessage(), e);
            throw new IllegalArgumentException("데이터베이스 오류: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DataSourceUtils.releaseConnection(connection, dataSource);
            } catch (Exception e) {
                String errorMsg = "executeDynamicFileQuery failed: Error closing resources for procedureCall: " + procedureCall;
                logger.error(errorMsg, e.getMessage(), e);
            }
        }

        return mappedResult;
    }

    public List<Map<String, Object>> executeDynamicQuery(String procedureCall) {
        List<Map<String, Object>> mappedResult = new ArrayList<>();
        Connection connection = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            // Modified: Extract procedureName safely, handling cases with no space
            int spaceIndex = procedureCall.indexOf(' ');
            String procedureName = spaceIndex != -1 ? procedureCall.substring(0, spaceIndex).trim() : procedureCall.trim();
            String paramString = spaceIndex != -1 ? procedureCall.substring(spaceIndex + 1).trim() : "";
            List<Object> params = parseParameters(paramString, Collections.emptyList());
            // Modified: Construct EXEC statement without curly braces for MSSQL
            String placeholders = String.join(",", Collections.nCopies(params.size(), "?"));
            String callString = "EXEC " + procedureName + (params.isEmpty() ? "" : " " + placeholders);

            connection = DataSourceUtils.getConnection(dataSource);
            if (connection == null) {
                String errorMsg = "executeDynamicQuery failed: Unable to obtain JDBC Connection from DataSource";
                logger.error(errorMsg);
                throw new IllegalStateException("Unable to obtain JDBC Connection from DataSource");
            }

            // Modified: Use plain EXEC statement without {} wrapping
            stmt = connection.prepareCall(callString);

            // Modified: Set parameters, handling quoted strings and byte[]
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    String value = ((String) param).replace("''", "'"); // Undo double-quote escaping
                    stmt.setString(i + 1, value);
                } else if (param instanceof byte[]) {
                    byte[] data = (byte[]) param;
                    if (data.length > fileConfig.getMaxFileSize()) {
                        logger.error("Parameter {} exceeds file size limit of {}MB", i + 1, fileConfig.getMaxFileSize() / (1024 * 1024));
                        throw new IllegalArgumentException("File size exceeds limit");
                    }
                    stmt.setBinaryStream(i + 1, new ByteArrayInputStream(data), data.length);
                } else {
                    stmt.setObject(i + 1, param);
                }
            }

            boolean hasResultSet = stmt.execute();
            if (!hasResultSet) {
                return Collections.emptyList();
            }

            rs = stmt.getResultSet();
            if (rs == null) {
                return Collections.emptyList();
            }

            List<String> columnNames = getColumnNames(rs);

            while (rs.next()) {
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < columnNames.size(); i++) {
                    Object value = rs.getObject(i + 1);
                    if ("FILEDATA".equalsIgnoreCase(columnNames.get(i)) && value instanceof Blob) {
                        Blob blob = (Blob) value;
                        try (InputStream inputStream = blob.getBinaryStream()) {
                            byte[] bytes = inputStream.readAllBytes();
                            if (bytes.length > fileConfig.getMaxFileSize()) {
                                logger.warn("Retrieved file size exceeds {}MB limit: {}", fileConfig.getMaxFileSize() / (1024 * 1024), bytes.length);
                            }
                            String base64Data = Base64.getEncoder().encodeToString(bytes);
                            rowMap.put(columnNames.get(i), base64Data);
                        } catch (IOException e) {
                            logger.error("Failed to convert FILEDATA to Base64", e);
                            rowMap.put(columnNames.get(i), "");
                        }
                    } else {
                        rowMap.put(columnNames.get(i), value == null ? "" : value);
                    }
                }
                mappedResult.add(rowMap);
                if (mappedResult.size() > fileConfig.getMaxResultSize()) {
                    logger.warn("Result size exceeds limit: {}", mappedResult.size());
                    break;
                }
            }

        } catch (Exception e) {
            String errorMsg = "executeDynamicQuery failed for procedureCall: " + procedureCall;
            logger.error(errorMsg, e.getMessage(), e);
            throw new IllegalArgumentException("데이터베이스 오류: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                DataSourceUtils.releaseConnection(connection, dataSource);
            } catch (Exception e) {
                String errorMsg = "executeDynamicQuery failed: Error closing resources for procedureCall: " + procedureCall;
                logger.error(errorMsg, e.getMessage(), e);
            }
        }

        return mappedResult;
    }

    private List<String> getColumnNames(ResultSet rs) {
        List<String> columnNames = new ArrayList<>();
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            if (columnCount == 0) {
                String errorMsg = "getColumnNames failed: No columns found in ResultSet";
                logger.warn(errorMsg);
                columnNames.add("col1");
                return columnNames;
            }
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i) != null ? metaData.getColumnLabel(i) : metaData.getColumnName(i);
                columnNames.add(columnName);
            }
        } catch (Exception e) {
            String errorMsg = "getColumnNames failed: Failed to extract column names";
            logger.error(errorMsg, e.getMessage(), e);
            columnNames.add("col1");
        }
        return columnNames;
    }

    private List<Object> parseParameters(String paramString, List<Object> originalParams) {
        List<Object> params = new ArrayList<>();
        if (paramString.isEmpty()) {
            return params;
        }

        // Modified: Handle quoted strings and unquoted byte[] parameters
        String[] paramArray = paramString.split(",\\s*(?=(?:(?:[^']*'){2})*[^']*$)"); // Split outside quotes
        int originalParamIndex = 0;

        for (String param : paramArray) {
            param = param.trim();
            if (param.startsWith("'") && param.endsWith("'")) {
                String value = param.substring(1, param.length() - 1).replace("''", "'"); // Remove quotes and unescape
                if ("DATA".equals(value) && originalParamIndex < originalParams.size() && originalParams.get(originalParamIndex) instanceof byte[]) {
                    params.add(originalParams.get(originalParamIndex));
                } else {
                    params.add(value);
                }
                originalParamIndex++;
            } else if (param.startsWith("[") && param.endsWith("]")) {
                try {
                    String base64 = param.substring(1, param.length() - 1);
                    byte[] decoded = Base64.getDecoder().decode(base64);
                    if (decoded.length > fileConfig.getMaxFileSize()) {
                        String errorMsg = "parseParameters failed: Base64 data exceeds " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB limit for param: " + param;
                        logger.error(errorMsg);
                        throw new IllegalArgumentException("Base64 data exceeds " + (fileConfig.getMaxFileSize() / (1024 * 1024)) + "MB limit");
                    }
                    params.add(decoded);
                    originalParamIndex++;
                } catch (IllegalArgumentException e) {
                    String errorMsg = "parseParameters failed: Failed to decode Base64 for param: " + param;
                    logger.error(errorMsg, e.getMessage(), e);
                    params.add(null);
                }
            } else {
                logger.warn("Invalid parameter format: {}", param);
                params.add(null);
            }
        }
        return params;
    }

    private String procedureTAB(String open, String close, String procedureCall) {
        int start = procedureCall.indexOf(open) + 1;
        int end = close.isEmpty() ? procedureCall.length() : procedureCall.lastIndexOf(close);
        if (start <= 0 || end < 0 || start >= end) { // Modified: Handle start <= 0
            return "";
        }
        return procedureCall.substring(start, end).trim();
    }
}