package com.virtual.cloud.om.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.sdk.api.UserApi;
import com.virtual.cloud.om.sdk.dto.UserRegisterRejectVO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDetailDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestVO;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.sdk.entity.mysql.UserRegisterRequest;
import com.virtual.cloud.om.sdk.mapper.SysUserMapper;
import com.virtual.cloud.om.sdk.mapper.UserRegisterRequestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 用户管理 Service 实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserApi {

    private final UserRegisterRequestMapper userRegisterRequestMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRegisterRequest(String username, String password, String remark) {
        // 1. 校验用户名是否已存在于 sys_user
        if (sysUserMapper.selectByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 2. 校验是否有 pending 状态的申请
        LambdaQueryWrapper<UserRegisterRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRegisterRequest::getUsername, username)
               .eq(UserRegisterRequest::getStatus, "pending");
        if (userRegisterRequestMapper.selectCount(wrapper) > 0) {
            throw new RuntimeException("该用户名有待审批的申请");
        }
        // 3. 统计历史被拒次数
        List<UserRegisterRequest> history = userRegisterRequestMapper.selectByUsernameOrderByTimeDesc(username);
        int rejectCount = (int) history.stream()
            .filter(r -> "rejected".equals(r.getStatus()))
            .count();
        // 4. 创建申请记录
        UserRegisterRequest request = new UserRegisterRequest();
        request.setId(UUID.randomUUID().toString());
        request.setUsername(username);
        request.setPassword(password);
        request.setRemark(remark);
        request.setStatus("pending");
        request.setRejectCount(rejectCount);
        request.setSubmitTime(LocalDateTime.now());
        userRegisterRequestMapper.insert(request);
        log.info("[UserService] 用户 {} 提交注册申请", username);
    }

    @Override
    public List<UserRegisterRequestVO> getPendingRegisterRequests() {
        LambdaQueryWrapper<UserRegisterRequest> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRegisterRequest::getStatus, "pending")
               .orderByDesc(UserRegisterRequest::getSubmitTime);
        List<UserRegisterRequest> list = userRegisterRequestMapper.selectList(wrapper);
        return convertToVOList(list);
    }

    @Override
    public UserRegisterRequestDetailDTO getRegisterRequestDetail(String id) {
        UserRegisterRequest request = userRegisterRequestMapper.selectById(id);
        if (request == null) {
            throw new RuntimeException("申请记录不存在");
        }
        // 查询该用户名的所有历史记录
        List<UserRegisterRequest> history = userRegisterRequestMapper.selectByUsernameOrderByTimeDesc(request.getUsername());
        List<UserRegisterRequestVO> historyVO = convertToVOList(history);
        // 转换为 DTO
        List<UserRegisterRequestDTO> historyDTO = new ArrayList<>();
        for (UserRegisterRequestVO vo : historyVO) {
            UserRegisterRequestDTO dto = new UserRegisterRequestDTO();
            BeanUtils.copyProperties(vo, dto);
            historyDTO.add(dto);
        }
        // 构建详情对象
        UserRegisterRequestDetailDTO detail = new UserRegisterRequestDetailDTO();
        BeanUtils.copyProperties(request, detail);
        detail.setHistoryList(historyDTO);
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveRegisterRequest(String id) {
        UserRegisterRequest request = userRegisterRequestMapper.selectById(id);
        if (request == null) {
            throw new RuntimeException("申请记录不存在");
        }
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("该申请已被审批");
        }
        // 1. 创建 sys_user 记录
        SysUser user = new SysUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus("active");
        sysUserMapper.insert(user);
        // 2. 更新申请状态为 approved
        request.setStatus("approved");
        request.setApproveTime(LocalDateTime.now());
        userRegisterRequestMapper.updateById(request);
        log.info("[UserService] 审批通过用户 {} 的注册申请", request.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectRegisterRequest(UserRegisterRejectVO vo) {
        UserRegisterRequest request = userRegisterRequestMapper.selectById(vo.getId());
        if (request == null) {
            throw new RuntimeException("申请记录不存在");
        }
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("该申请已被审批");
        }
        request.setStatus("rejected");
        request.setRejectReason(vo.getRejectReason());
        request.setApproveTime(LocalDateTime.now());
        userRegisterRequestMapper.updateById(request);
        log.info("[UserService] 审批拒绝用户 {} 的注册申请，拒绝原因: {}",
            request.getUsername(), vo.getRejectReason());
    }

    @Override
    public List<String> getUserList() {
        return sysUserMapper.selectList(null).stream()
            .map(SysUser::getUsername)
            .collect(Collectors.toList());
    }

    private List<UserRegisterRequestVO> convertToVOList(List<UserRegisterRequest> list) {
        List<UserRegisterRequestVO> voList = new ArrayList<>();
        for (UserRegisterRequest entity : list) {
            UserRegisterRequestVO vo = new UserRegisterRequestVO();
            BeanUtils.copyProperties(entity, vo);
            if (entity.getSubmitTime() != null) {
                vo.setSubmitTime(entity.getSubmitTime().toString());
            }
            if (entity.getApproveTime() != null) {
                vo.setApproveTime(entity.getApproveTime().toString());
            }
            voList.add(vo);
        }
        return voList;
    }
}
