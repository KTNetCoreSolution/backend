package com.boot.ktn.entity.auth;

import lombok.Data;

@Data
public class LoginEntity {
    private String empNo;
    private String empNm;
    private String empPwd;
    private String auth;
    private String levelCd;
    private String orgCd;
    private String orgNm;
    private String carOrgCd;
    private String carOrgNm;
    private String carMngOrgNm;
    private String carMngOrgCd;
    private String standardSectionCd;
    private String standardSectionNm;
    private String pwdChgYn;
    private String clientIP;
}
