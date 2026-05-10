package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.UserRegisterRejectVO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDetailDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestVO;

import java.util.List;

/**
 * 用户相关 API 接口定义
 */
public interface UserApi {

    /**
     * 提交用户注册申请
     * @param username 用户名
     * @param password 密码（SM4加密）
     * @param remark 备注
     */
    void submitRegisterRequest(String username, String password, String remark);

    /**
     * 获取待审批注册申请列表
     * @return 待审批申请列表
     */
    List<UserRegisterRequestVO> getPendingRegisterRequests();

    /**
     * 获取注册申请详情（含历史记录）
     * @param id 申请ID
     * @return 申请详情
     */
    UserRegisterRequestDetailDTO getRegisterRequestDetail(String id);

    /**
     * 同意注册申请
     * @param id 申请ID
     */
    void approveRegisterRequest(String id);

    /**
     * 拒绝注册申请
     * @param vo 拒绝请求
     */
    void rejectRegisterRequest(UserRegisterRejectVO vo);

    /**
     * 获取用户列表（仅admin）
     * @return 用户名列表
     */
    List<String> getUserList();
}
