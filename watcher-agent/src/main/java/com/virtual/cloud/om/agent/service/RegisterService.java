package com.virtual.cloud.om.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.virtual.cloud.om.sdk.constant.RegisterStatusEnum;
import com.virtual.cloud.om.sdk.dto.RegisterApprovalDTO;
import com.virtual.cloud.om.sdk.dto.RegisterRequestDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDTO;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.sdk.entity.mysql.UserRegisterRequest;
import com.virtual.cloud.om.sdk.exception.AppException;
import com.virtual.cloud.om.sdk.exception.ErrorCodes;
import com.virtual.cloud.om.sdk.mapper.SysUserMapper;
import com.virtual.cloud.om.sdk.mapper.UserRegisterRequestMapper;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegisterService {

    @Autowired
    private UserRegisterRequestMapper registerRequestMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequestDTO request) {
        if (StringUtils.isBlank(request.getUsername())) {
            throw new AppException(ErrorCodes.USERNAME_BLANK);
        }
        if (StringUtils.isBlank(request.getPassword())) {
            throw new AppException(ErrorCodes.PARAMETER_VALUE_IS_NULL);
        }
        if (!StringUtils.equals(request.getPassword(), request.getConfirmPassword())) {
            throw new AppException(ErrorCodes.PASSWORD_NOT_MATCH);
        }
        SysUser existUser = findUserByUserName(request.getUsername());
        if (Objects.nonNull(existUser)) {
            throw new AppException(ErrorCodes.USERNAME_ALREADY_EXISTS);
        }
        LambdaQueryWrapper<UserRegisterRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRegisterRequest::getUsername, request.getUsername())
                .eq(UserRegisterRequest::getStatus, RegisterStatusEnum.PENDING.getCode());
        long pendingCount = registerRequestMapper.selectCount(queryWrapper);
        if (pendingCount > 0) {
            throw new AppException(ErrorCodes.USERNAME_ALREADY_EXISTS);
        }
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .id(IdUtil.fastSimpleUUID())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(RegisterStatusEnum.PENDING.getCode())
                .submitTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        registerRequestMapper.insert(registerRequest);
        log.info("[Register] 注册申请成功: username={}", request.getUsername());
    }

    public IPage<UserRegisterRequestDTO> getPendingList(int pageNum, int pageSize) {
        LambdaQueryWrapper<UserRegisterRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserRegisterRequest::getStatus, RegisterStatusEnum.PENDING.getCode())
                .orderByDesc(UserRegisterRequest::getSubmitTime);
        Page<UserRegisterRequest> page = new Page<>(pageNum, pageSize);
        Page<UserRegisterRequest> result = registerRequestMapper.selectPage(page, queryWrapper);
        return convertToDTOPage(result);
    }

    public IPage<UserRegisterRequestDTO> getAllList(int pageNum, int pageSize) {
        LambdaQueryWrapper<UserRegisterRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(UserRegisterRequest::getSubmitTime);
        Page<UserRegisterRequest> page = new Page<>(pageNum, pageSize);
        Page<UserRegisterRequest> result = registerRequestMapper.selectPage(page, queryWrapper);
        return convertToDTOPage(result);
    }

    public UserRegisterRequestDTO getDetail(String id) {
        UserRegisterRequest request = registerRequestMapper.selectById(id);
        if (Objects.isNull(request)) {
            throw new AppException(ErrorCodes.REGISTRATION_NOT_FOUND);
        }
        return convertToDTO(request);
    }

    @Transactional(rollbackFor = Exception.class)
    public void approve(String id, String approver) {
        UserRegisterRequest registerRequest = registerRequestMapper.selectById(id);
        if (Objects.isNull(registerRequest)) {
            throw new AppException(ErrorCodes.REGISTRATION_NOT_FOUND);
        }
        if (!RegisterStatusEnum.PENDING.getCode().equals(registerRequest.getStatus())) {
            throw new AppException(ErrorCodes.REGISTRATION_ALREADY_PROCESSED);
        }
        SysUser newUser = SysUser.builder()
                .id(IdUtil.fastSimpleUUID())
                .username(registerRequest.getUsername())
                .password(registerRequest.getPassword())
                .status("active")
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        sysUserMapper.insert(newUser);
        registerRequest.setStatus(RegisterStatusEnum.APPROVED.getCode());
        registerRequest.setApprover(approver);
        registerRequest.setApproveTime(LocalDateTime.now());
        registerRequest.setUpdateTime(LocalDateTime.now());
        registerRequestMapper.updateById(registerRequest);
        log.info("[Approve] 审批通过: id={}, username={}, approver={}", id, registerRequest.getUsername(), approver);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reject(RegisterApprovalDTO approvalDTO, String approver) {
        UserRegisterRequest registerRequest = registerRequestMapper.selectById(approvalDTO.getId());
        if (Objects.isNull(registerRequest)) {
            throw new AppException(ErrorCodes.REGISTRATION_NOT_FOUND);
        }
        if (!RegisterStatusEnum.PENDING.getCode().equals(registerRequest.getStatus())) {
            throw new AppException(ErrorCodes.REGISTRATION_ALREADY_PROCESSED);
        }
        registerRequest.setStatus(RegisterStatusEnum.REJECTED.getCode());
        registerRequest.setApprover(approver);
        registerRequest.setApproveTime(LocalDateTime.now());
        registerRequest.setRejectReason(approvalDTO.getRejectReason());
        registerRequest.setRemark(approvalDTO.getRemark());
        registerRequest.setUpdateTime(LocalDateTime.now());
        registerRequestMapper.updateById(registerRequest);
        log.info("[Reject] 审批拒绝: id={}, username={}, approver={}, reason={}",
                approvalDTO.getId(), registerRequest.getUsername(), approver, approvalDTO.getRejectReason());
    }

    public IPage<SysUser> getUserList(int pageNum, int pageSize) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        return sysUserMapper.selectPage(page, queryWrapper);
    }

    public SysUser getUserDetail(String id) {
        SysUser user = sysUserMapper.selectById(id);
        if (Objects.isNull(user)) {
            throw new AppException(ErrorCodes.USER_DOES_NOT_EXIT);
        }
        return user;
    }

    public SysUser findUserByUserName(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(queryWrapper);
    }

    private UserRegisterRequestDTO convertToDTO(UserRegisterRequest request) {
        UserRegisterRequestDTO dto = new UserRegisterRequestDTO();
        BeanUtils.copyProperties(request, dto);
        RegisterStatusEnum statusEnum = RegisterStatusEnum.getByCode(request.getStatus());
        dto.setStatusDesc(statusEnum != null ? statusEnum.getDesc() : request.getStatus());
        return dto;
    }

    private IPage<UserRegisterRequestDTO> convertToDTOPage(Page<UserRegisterRequest> page) {
        List<UserRegisterRequestDTO> dtoList = page.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        Page<UserRegisterRequestDTO> dtoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        dtoPage.setRecords(dtoList);
        return dtoPage;
    }
}
