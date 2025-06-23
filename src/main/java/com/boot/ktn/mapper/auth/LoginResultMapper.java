package com.boot.ktn.mapper.auth;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface LoginResultMapper {
    @Select("CALL UP_LOGINHIST_TRANSACTION(#{empNo}, #{ip}, #{userConGb}, #{userAgent});")
    Map<String, Object> loginResultProcedure(@Param("empNo") String empNo,
                                       @Param("ip") String ip,
                                       @Param("userConGb") String userConGb,
                                       @Param("userAgent") String userAgent);
}
