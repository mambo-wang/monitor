package com.virtual.cloud.om.performer.controller;
 
import com.virtual.cloud.om.performer.entity.UserInfo;
import com.virtual.cloud.om.performer.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
 
import javax.annotation.Resource;
import java.util.List;
 
@RestController
public class UserInfoController {
 
    @Resource
    private UserInfoService userInfoService ;
 
    //localhost:7010/getById?id=1
    @GetMapping("/getById")
    public UserInfo getById (Long id) {
        return userInfoService.selectById(id) ;
    }
 
    @GetMapping("/getList")
    public List<UserInfo> getList () {
        UserInfo userInfo = new UserInfo() ;
        userInfo.setId(System.currentTimeMillis());
        userInfo.setUserName("xiaolin");
        userInfo.setPassWord("54321");
        userInfo.setPhone("18500909876");
        userInfo.setCreateDay("2022-02-06");
        userInfoService.saveData(userInfo);

        return userInfoService.selectList() ;
    }
 
}
 
 