package com.boot.ktn.mapper.auth;

import com.boot.ktn.entity.auth.LoginEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    @Select("""
        SELECT
          a.EMPNO, a.EMPNM, a.EMPPWD, ISNULL(b.AUTHID, '') AS AUTH,
          a.ORGCD, c.NAME AS ORGNM, ISNULL(a.PWDCHGYN, '') AS PWDCHGYN,
          ISNULL(car.CARORGCD, '') AS CARORGCD,
          ISNULL(car.CARORGNM, '') AS CARORGNM
        FROM tb_userinfo a
        LEFT JOIN tb_userauthgroup b ON a.EMPNO = b.EMPNO
        LEFT JOIN tb_ktnorg c ON a.ORGCD = c.CODE
        OUTER APPLY (
            SELECT
              STRING_AGG(ISNULL(e.ORGCD, d.ORGCD), ',') AS CARORGCD,
              STRING_AGG(ISNULL(g.NAME, f.NAME), ',') AS CARORGNM
            FROM tb_userinfo_fix d
            LEFT JOIN tb_moduleorgauthinfo e ON a.EMPNO = e.AUTHOPERATOR AND e.MODULETYPE = 'CAR'
            LEFT JOIN tb_ktnorg_fix f ON d.ORGCD = f.CODE
            LEFT JOIN tb_ktnorg_fix g ON e.ORGCD = g.CODE
            WHERE a.EMPNO = d.EMPNO
        ) car
        WHERE a.EMPNO = #{empNo}
        AND a.EMPPWD = #{empPw}
    """)
    LoginEntity loginCheck(@Param("empNo") String empNo, @Param("empPw") String empPw);

    @Select("""
        SELECT
          a.EMPNO, a.EMPNM, a.EMPPWD, ISNULL(b.AUTHID, '') AS AUTH,
          a.ORGCD, c.NAME AS ORGNM, ISNULL(a.PWDCHGYN, '') AS PWDCHGYN,
          ISNULL(car.CARORGCD, '') AS CARORGCD,
          ISNULL(car.CARORGNM, '') AS CARORGNM
        FROM tb_userinfo a
        LEFT JOIN tb_userauthgroup b ON a.EMPNO = b.EMPNO
        LEFT JOIN tb_ktnorg c ON a.ORGCD = c.CODE
        OUTER APPLY (
            SELECT
              STRING_AGG(ISNULL(e.ORGCD, d.ORGCD), ',') AS CARORGCD,
              STRING_AGG(ISNULL(g.NAME, f.NAME), ',') AS CARORGNM
            FROM tb_userinfo_fix d
            LEFT JOIN tb_moduleorgauthinfo e ON a.EMPNO = e.AUTHOPERATOR AND e.MODULETYPE = 'CAR'
            LEFT JOIN tb_ktnorg_fix f ON d.ORGCD = f.CODE
            LEFT JOIN tb_ktnorg_fix g ON e.ORGCD = g.CODE
            WHERE a.EMPNO = d.EMPNO
        ) car
        WHERE a.EMPNO = #{empNo}
    """)
    LoginEntity loginCheckManager(@Param("empNo") String empNo);
}
