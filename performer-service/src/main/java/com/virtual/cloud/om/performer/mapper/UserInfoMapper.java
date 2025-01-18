package com.virtual.cloud.om.performer.mapper;
 
import com.virtual.cloud.om.performer.entity.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
 
@Mapper
public interface UserInfoMapper {
    void saveData (UserInfo userInfo) ;
    UserInfo selectById (@Param("id") Long id) ;
    List<UserInfo> selectList () ;
}