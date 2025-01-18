package com.virtual.cloud.om.performer.service;
 
import com.virtual.cloud.om.performer.entity.UserInfo;
import com.virtual.cloud.om.performer.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;
 
import javax.annotation.Resource;
import java.util.List;
 
@Service
public class UserInfoService {
 
    @Resource
    private UserInfoMapper userInfoMapper ;
 
    public void saveData(UserInfo userInfo) {
        userInfoMapper.saveData(userInfo);
    }
 
    public UserInfo selectById(Long id) {
        return userInfoMapper.selectById(id);
    }
 
    public List<UserInfo> selectList() {
        return userInfoMapper.selectList();
    }
 
}
 
 