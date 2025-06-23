package com.boot.ktn.entity.auth;

import lombok.Data;

@Data
public class AuthEntity {
    private String empNo;
    private String empNm;
    private String auth;
    private String clientIP;
}
