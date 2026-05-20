package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.api.UserApi;
import com.virtual.cloud.om.sdk.dto.UserRegisterRejectVO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDetailDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestVO;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理 Controller
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApi userApi;

    @Operation(summary = "提交注册申请")
    @PostMapping("/register")
    public RpcResult<Void> submitRegister(@RequestParam String username,
                                          @RequestParam String password,
                                          @RequestParam(required = false) String remark) {
        userApi.submitRegisterRequest(username, password, remark);
        return RpcResult.success();
    }

    @Operation(summary = "查询待审批列表")
    @GetMapping("/register/pending")
    public RpcResult<List<UserRegisterRequestVO>> getPendingRequests() {
        List<UserRegisterRequestVO> list = userApi.getPendingRegisterRequests();
        return RpcResult.success(list);
    }

    @Operation(summary = "查询申请详情（含历史）")
    @GetMapping("/register/{id}")
    public RpcResult<UserRegisterRequestDetailDTO> getRegisterDetail(@PathVariable String id) {
        UserRegisterRequestDetailDTO detail = userApi.getRegisterRequestDetail(id);
        return RpcResult.success(detail);
    }

    @Operation(summary = "同意注册申请")
    @PostMapping("/register/{id}/approve")
    public RpcResult<Void> approveRegister(@PathVariable String id) {
        userApi.approveRegisterRequest(id);
        return RpcResult.success();
    }

    @Operation(summary = "拒绝注册申请")
    @PostMapping("/register/reject")
    public RpcResult<Void> rejectRegister(@RequestBody UserRegisterRejectVO vo) {
        userApi.rejectRegisterRequest(vo);
        return RpcResult.success();
    }

    @Operation(summary = "获取用户列表")
    @GetMapping("/list")
    public RpcResult<List<String>> getUserList() {
        List<String> list = userApi.getUserList();
        return RpcResult.success(list);
    }
}
