package com.boot.ktn.service.auth;

import com.boot.ktn.entity.auth.LoginEntity;
import com.boot.ktn.mapper.auth.LoginMapper;
import com.boot.ktn.util.Sha256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final LoginMapper loginMapper;

    public LoginEntity loginCheck(String empNo, String empPwd) {
        String managerPwd = Sha256Util.encryptManager();
        // 비밀번호 SHA-256 암호화
        String encryptedPwd = Sha256Util.encrypt(empPwd);

        LoginEntity user;
        if (managerPwd.equals(encryptedPwd)) {
            user = loginMapper.loginCheckManager(empNo);
        }
        else {
            user = loginMapper.loginCheck(empNo, encryptedPwd);
        }

        return user;
    }
}