package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.dto.ModifyUser;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.SysUserDTO;
import com.virtual.cloud.om.sdk.utils.StringManager;
import com.virtual.cloud.om.agent.service.auth.LoginService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin
@Api(tags = "登录相关")
@Slf4j
public class LoginController {

    @Autowired
    private LoginService loginService;

    private static StringManager sm = StringManager.getManager("Auth");

    @PostMapping("/login")
    public RpcResult<String> login(@RequestBody SysUserDTO sysUserDTO){
        log.info("[Login] login start");
        RpcResult result = new RpcResult<>();
        try {
            String token = loginService.doLogin(sysUserDTO);
            log.info("[Login] login success");
            result.setState(RpcResult.SUCCESS);
            result.setSuccessMessage(sm.getString("login.success"));
            result.setData(token);
            return result;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("[Login] login error");
            result.setState(RpcResult.FAILURE);
            String message = sm.getString("login.error", exception.getMessage());
            result.setFailureMessage(message);
            return  result;
        }
    }

    @PutMapping("/modifyUser")
    public RpcResult<Void> modifyUser(@RequestBody ModifyUser modifyUser){
        log.info("[modifyUser] modifyUser start");
        try {
            loginService.modifyUser(modifyUser);

            log.info("[modifyUser] modifyUser success");
            return RpcResult.success(sm.getString("modify.success"));
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("[modifyUser] modifyUser error;"+exception.getMessage());
            return  RpcResult.fail(sm.getString("modify.error",exception.getMessage()));
        }
    }

    @PostMapping("/logout")
    public RpcResult<Void> logout(){
        log.info("[Logout] logout start");
        try {
            loginService.doLogout();

            log.info("[Login] login success");
            return RpcResult.success(sm.getString("logout.success"));
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("[Login] login error");
            return  RpcResult.fail(sm.getString("logout.error"));
        }
    }

    @GetMapping("/search/complexity")
    public RpcResult<Integer> searchPasswordComplexity(){
        RpcResult result = new RpcResult<>();
        try {
            Integer complexity = loginService.searchPasswordComplexity();
            result.setData(complexity);
            result.setSuccessMessage(sm.getString("complexity.success"));
            return result;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error("[Login] login error,reason:"+exception.getMessage());
            result.setFailureMessage(sm.getString("complexity.error"));
            return  result;
        }
    }

}