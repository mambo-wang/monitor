package com.virtual.cloud.om.agent.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.virtual.cloud.om.sdk.constant.Constant;
import com.virtual.cloud.om.sdk.dto.RpcResult;
import com.virtual.cloud.om.sdk.dto.RegisterApprovalDTO;
import com.virtual.cloud.om.sdk.dto.RegisterRequestDTO;
import com.virtual.cloud.om.sdk.dto.StateResult;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDTO;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.agent.service.RegisterService;
import com.virtual.cloud.om.sdk.utils.JwtTokenUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Api(tags = "用户注册审批")
@RestController
@RequestMapping("/user")
@Slf4j
public class RegisterController {

    @Autowired
    private RegisterService registerService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public RpcResult<Void> register(@RequestBody RegisterRequestDTO request) {
        try {
            registerService.register(request);
            RpcResult<Void> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setMessage("注册成功，请等待审批");
            return result;
        } catch (Exception e) {
            log.error("[Register] 注册失败: {}", e.getMessage(), e);
            RpcResult<Void> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("获取待审批列表")
    @GetMapping("/registration/pending")
    public RpcResult<IPage<UserRegisterRequestDTO>> getPendingList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            IPage<UserRegisterRequestDTO> page = registerService.getPendingList(pageNum, pageSize);
            RpcResult<IPage<UserRegisterRequestDTO>> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setData(page);
            return result;
        } catch (Exception e) {
            log.error("[GetPendingList] 获取待审批列表失败: {}", e.getMessage(), e);
            RpcResult<IPage<UserRegisterRequestDTO>> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("获取所有审批记录")
    @GetMapping("/registration/list")
    public RpcResult<IPage<UserRegisterRequestDTO>> getAllList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            IPage<UserRegisterRequestDTO> page = registerService.getAllList(pageNum, pageSize);
            RpcResult<IPage<UserRegisterRequestDTO>> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setData(page);
            return result;
        } catch (Exception e) {
            log.error("[GetAllList] 获取审批列表失败: {}", e.getMessage(), e);
            RpcResult<IPage<UserRegisterRequestDTO>> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("获取审批详情")
    @GetMapping("/registration/{id}")
    public RpcResult<UserRegisterRequestDTO> getDetail(@PathVariable String id) {
        try {
            UserRegisterRequestDTO dto = registerService.getDetail(id);
            RpcResult<UserRegisterRequestDTO> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setData(dto);
            return result;
        } catch (Exception e) {
            log.error("[GetDetail] 获取审批详情失败: {}", e.getMessage(), e);
            RpcResult<UserRegisterRequestDTO> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("审批通过")
    @PostMapping("/registration/approve/{id}")
    public RpcResult<Void> approve(@PathVariable String id) {
        try {
            String approver = getCurrentUsername();
            registerService.approve(id, approver);
            RpcResult<Void> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setMessage("审批通过");
            return result;
        } catch (Exception e) {
            log.error("[Approve] 审批失败: {}", e.getMessage(), e);
            RpcResult<Void> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("审批拒绝")
    @PostMapping("/registration/reject")
    public RpcResult<Void> reject(@RequestBody RegisterApprovalDTO approvalDTO) {
        try {
            String approver = getCurrentUsername();
            registerService.reject(approvalDTO, approver);
            RpcResult<Void> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setMessage("已拒绝");
            return result;
        } catch (Exception e) {
            log.error("[Reject] 拒绝失败: {}", e.getMessage(), e);
            RpcResult<Void> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("获取用户列表")
    @GetMapping("/list")
    public RpcResult<IPage<SysUser>> getUserList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            IPage<SysUser> page = registerService.getUserList(pageNum, pageSize);
            RpcResult<IPage<SysUser>> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setData(page);
            return result;
        } catch (Exception e) {
            log.error("[GetUserList] 获取用户列表失败: {}", e.getMessage(), e);
            RpcResult<IPage<SysUser>> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public RpcResult<SysUser> getUserDetail(@PathVariable String id) {
        try {
            SysUser user = registerService.getUserDetail(id);
            RpcResult<SysUser> result = new RpcResult<>();
            result.setState(StateResult.SUCCESS);
            result.setData(user);
            return result;
        } catch (Exception e) {
            log.error("[GetUserDetail] 获取用户详情失败: {}", e.getMessage(), e);
            RpcResult<SysUser> result = new RpcResult<>();
            result.setState(StateResult.FAILURE);
            result.setFailureMessage(e.getMessage());
            return result;
        }
    }

    private String getCurrentUsername() {
        try {
            String token = Constant.username;
            if (Objects.nonNull(token)) {
                return token;
            }
        } catch (Exception e) {
            log.warn("[GetCurrentUsername] 获取当前用户失败: {}", e.getMessage());
        }
        return "unknown";
    }
}
