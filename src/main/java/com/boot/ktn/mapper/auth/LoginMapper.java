package com.boot.ktn.mapper.auth;

import com.boot.ktn.entity.auth.LoginEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    @Select("""
        SELECT a.EMPNO, a.EMPNM, a.EMPPWD, ISNULL(b.AUTHID, '') AUTH, a.ORGCD ,c.NAME AS ORGNM, ISNULL(a.PWDCHGYN, '') PWDCHGYN
        FROM tb_userinfo a
        LEFT JOIN tb_userauthgroup b ON a.EMPNO = b.EMPNO
        LEFT JOIN tb_ktnorg c ON a.ORGCD = c.CODE
        WHERE a.EMPNO = #{empNo}
        AND a.EMPPWD = #{empPw}
    """)
    LoginEntity loginCheck(@Param("empNo") String empNo, @Param("empPw") String empPw);

    @Select("""
        SELECT a.EMPNO, a.EMPNM, a.EMPPWD, ISNULL(b.AUTHID, '') AUTH, a.ORGCD ,c.NAME AS ORGNM, ISNULL(a.PWDCHGYN, '') PWDCHGYN
        FROM tb_userinfo a
        LEFT JOIN tb_userauthgroup b ON a.EMPNO = b.EMPNO
        LEFT JOIN tb_ktnorg c ON a.ORGCD = c.CODE
        WHERE a.EMPNO = #{empNo}
    """)
    LoginEntity loginCheckManager(@Param("empNo") String empNo);
}
