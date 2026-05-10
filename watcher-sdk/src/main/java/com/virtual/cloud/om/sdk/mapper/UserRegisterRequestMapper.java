package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.UserRegisterRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户注册申请 Mapper 接口
 */
@Mapper
public interface UserRegisterRequestMapper extends BaseMapper<UserRegisterRequest> {

    /**
     * 查询某用户名所有申请记录（按时间倒序）
     * @param username 用户名
     * @return 申请记录列表
     */
    @Select("SELECT * FROM user_register_request WHERE username = #{username} ORDER BY create_time DESC")
    List<UserRegisterRequest> selectByUsernameOrderByTimeDesc(@Param("username") String username);

}
