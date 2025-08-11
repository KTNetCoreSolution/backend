package com.boot.ktn.mapper.auth;

import com.boot.ktn.entity.auth.LoginEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LoginMapper {
    @Select("""
		SELECT
          a.EMPNO AS empNo,
          a.EMPNM AS empNm,
          a.EMPPWD AS empPwd,
          ISNULL(b.AUTHID, '') AS auth,
          a.ORGCD AS orgCd,
          c.NAME AS orgNm,
          ISNULL(fixuser.ORGCD, '') AS carOrgCd,
          ISNULL(fixorg.NAME, '') AS carOrgNm,
          ISNULL(car.CARMNGORGCD, '') AS carMngOrgCd,
          ISNULL(car.CARMNGORGNM, '') AS carMngOrgNm,
          ISNULL(st.SECTIONCD, '') AS standardSectionCd,
          ISNULL(st.SECTIONNM, '') AS standardSectionNm,
          ISNULL(a.PWDCHGYN, '') AS pwdChgYn
        FROM tb_userinfo a
        LEFT JOIN tb_userauthgroup b ON a.EMPNO = b.EMPNO
        LEFT JOIN tb_ktnorg c ON a.ORGCD = c.CODE
        OUTER APPLY (
            SELECT
              STRING_AGG(ISNULL(e.ORGCD, d.ORGCD), ',') AS CARMNGORGCD,
              STRING_AGG(ISNULL(g.NAME, f.NAME), ',') AS CARMNGORGNM
            FROM tb_userinfo_fix d
            LEFT JOIN tb_moduleorgauthinfo e ON a.EMPNO = e.AUTHOPERATOR AND e.MODULETYPE = 'CAR'
            LEFT JOIN tb_ktnorg_fix f ON d.ORGCD = f.CODE
            LEFT JOIN tb_ktnorg_fix g ON e.ORGCD = g.CODE
            WHERE a.EMPNO = d.EMPNO
        ) car
        LEFT JOIN tb_userinfo_fix fixuser ON a.EMPNO = fixuser.EMPNO
        LEFT JOIN tb_ktnorg_fix fixorg ON fixuser.ORGCD = fixorg.CODE
        LEFT JOIN tb_standardactivity_usersection st ON a.EMPNO = st.EMPNO
        WHERE a.EMPNO = #{empNo}
        AND a.EMPPWD = #{empPw}
    """)
    LoginEntity loginCheck(@Param("empNo") String empNo, @Param("empPw") String empPw);

    @Select("""
		SELECT
          a.EMPNO AS empNo,
          a.EMPNM AS empNm,
          a.EMPPWD AS empPwd,
          ISNULL(b.AUTHID, '') AS auth,
          a.ORGCD AS orgCd,
          c.NAME AS orgNm,
          ISNULL(fixuser.ORGCD, '') AS carOrgCd,
          ISNULL(fixorg.NAME, '') AS carOrgNm,
          ISNULL(car.CARMNGORGCD, '') AS carMngOrgCd,
          ISNULL(car.CARMNGORGNM, '') AS carMngOrgNm,
          ISNULL(st.SECTIONCD, '') AS standardSectionCd,
          ISNULL(st.SECTIONNM, '') AS standardSectionNm,
          ISNULL(a.PWDCHGYN, '') AS pwdChgYn
        FROM tb_userinfo a
        LEFT JOIN tb_userauthgroup b ON a.EMPNO = b.EMPNO
        LEFT JOIN tb_ktnorg c ON a.ORGCD = c.CODE
        OUTER APPLY (
            SELECT
              STRING_AGG(ISNULL(e.ORGCD, d.ORGCD), ',') AS CARMNGORGCD,
              STRING_AGG(ISNULL(g.NAME, f.NAME), ',') AS CARMNGORGNM
            FROM tb_userinfo_fix d
            LEFT JOIN tb_moduleorgauthinfo e ON a.EMPNO = e.AUTHOPERATOR AND e.MODULETYPE = 'CAR'
            LEFT JOIN tb_ktnorg_fix f ON d.ORGCD = f.CODE
            LEFT JOIN tb_ktnorg_fix g ON e.ORGCD = g.CODE
            WHERE a.EMPNO = d.EMPNO
        ) car
        LEFT JOIN tb_userinfo_fix fixuser ON a.EMPNO = fixuser.EMPNO
        LEFT JOIN tb_ktnorg_fix fixorg ON fixuser.ORGCD = fixorg.CODE
        LEFT JOIN tb_standardactivity_usersection st ON a.EMPNO = st.EMPNO
        WHERE a.EMPNO = #{empNo}
    """)
    LoginEntity loginCheckManager(@Param("empNo") String empNo);
}
