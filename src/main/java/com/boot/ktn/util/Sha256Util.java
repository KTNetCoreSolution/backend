package com.boot.ktn.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Sha256Util {
    private static final Logger logger = LoggerFactory.getLogger(Sha256Util.class);

    public static String encrypt(String text) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            String errorMessage = "SHA-256 암호화 오류: ";
            logger.error(errorMessage, e.getMessage(), e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    /**
     * 'new1234+yyyyMMdd+!' 패턴의 문자열을 생성한 후 암호화합니다.
     * @return SHA-256 해시값
     */
    public static String encryptManager() {
        // 오늘 날짜 구하기
        LocalDate today = LocalDate.now();
        String dateStr = today.format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd

        // 문자열 조합
        String rawString = "new1234" + dateStr + "!";

        // 암호화하여 반환
        return encrypt(rawString);
    }
}
