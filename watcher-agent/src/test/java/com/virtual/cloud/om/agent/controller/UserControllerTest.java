package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.api.UserApi;
import com.virtual.cloud.om.sdk.dto.UserRegisterRejectVO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDetailDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserController 单元测试（TDD）
 * 红 → 绿 → 重构
 * 测试 Controller 方法级别的行为
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserController 单元测试")
class UserControllerTest {

    @Mock
    private UserApi userApi;

    @InjectMocks
    private UserController userController;

    // =================== POST /user/register ===================

    @Nested
    @DisplayName("submitRegister")
    class SubmitRegister {

        @Test
        @DisplayName("正常提交申请成功")
        void submit_success() {
            doNothing().when(userApi).submitRegisterRequest(any(), any(), any());
            userController.submitRegister("user1", "pwd", "备注");
            verify(userApi).submitRegisterRequest(eq("user1"), eq("pwd"), eq("备注"));
        }

        @Test
        @DisplayName("remark 为空正常提交")
        void submit_without_remark() {
            doNothing().when(userApi).submitRegisterRequest(any(), any(), any());
            userController.submitRegister("user1", "pwd", null);
            verify(userApi).submitRegisterRequest(eq("user1"), eq("pwd"), isNull());
        }

        @Test
        @DisplayName("用户名已存在时抛异常")
        void submit_throws_when_user_exists() {
            doThrow(new RuntimeException("用户名已存在")).when(userApi).submitRegisterRequest(any(), any(), any());
            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userController.submitRegister("exists", "pwd", null);
            assertEquals("用户名已存在", ex.getMessage());
        }
    }

    // =================== GET /user/register/pending ===================

    @Nested
    @DisplayName("getPendingRequests")
    class GetPendingRequests {

        @Test
        @DisplayName("返回 pending 列表")
        void return_pending_list() {
            UserRegisterRequestVO v1 = new UserRegisterRequestVO();
            v1.setId("id1");
            v1.setUsername("user1");
            v1.setStatus("pending");
            when(userApi.getPendingRegisterRequests()).thenReturn(Arrays.asList(v1));

            var result = userController.getPendingRequests();

            assertEquals(1, result.size());
            assertEquals("id1", result.get(0).getId());
        }

        @Test
        @DisplayName("空列表")
        void return_empty_list() {
            when(userApi.getPendingRegisterRequests()).thenReturn(Collections.emptyList());

            var result = userController.getPendingRequests();

            assertTrue(result.isEmpty());
        }
    }

    // =================== GET /user/register/{id} ===================

    @Nested
    @DisplayName("getRegisterDetail")
    class GetRegisterDetail {

        @Test
        @DisplayName("正常返回详情")
        void return_detail() {
            UserRegisterRequestDetailDTO dto = new UserRegisterRequestDTO();
            dto.setId("id1");
            dto.setUsername("user1");
            dto.setStatus("pending");
            dto.setRejectCount(2);
            when(userApi.getRegisterRequestDetail("id1")).thenReturn(dto);

            var result = userController.getRegisterDetail("id1");

            assertEquals("id1", result.getId());
            assertEquals("pending", result.getStatus());
        }

        @Test
        @DisplayName("记录不存在时抛异常")
        void throw_when_not_found() {
            when(userApi.getRegisterRequestDetail("nonexist")
                    .thenThrow(new RuntimeException("申请记录不存在"));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userController.getRegisterRequestDetail("nonexist"));
            assertEquals("申请记录不存在", ex.getMessage());
        }
    }

    // =================== POST /user/register/{id}/approve ===================

    @Nested
    @DisplayName("approveRegister")
    class ApproveRegister {

        @Test
        @DisplayName("正常审批通过")
        void approve_success() {
            doNothing().when(userApi).approveRegisterRequest("id1");
            userController.approveRegister("id1");
            verify(userApi).approveRegisterRequest("id1");
        }

        @Test
        @DisplayName("重复审批抛异常")
        void throw_when_already_approved() {
            doThrow(new RuntimeException("该申请已被审批")).when(userApi).approveRegisterRequest("id1");

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userController.approveRegister("id1"));
            assertEquals("该申请已被审批", ex.getMessage());
        }
    }

    // =================== POST /user/register/reject ===================

    @Nested
    @DisplayName("rejectRegister")
    class RejectRegister {

        @Test
        @DisplayName("正常拒绝")
        void reject_success() {
            UserRegisterRejectVO vo = new UserRegisterRejectVO();
            doNothing().when(userApi).rejectRegisterRequest(any(UserRegisterRejectVO.class));
            userController.rejectRegister(vo);
            verify(userApi).rejectRegisterRequest(any(UserRegisterRejectVO.class));
        }

        @Test
        @DisplayName("重复拒绝抛异常")
        void throw_when_already_rejected() {
            UserRegisterRejectVO vo = new UserRegisterRejectVO();
            doThrow(new RuntimeException("该申请已被审批"))
                    .when(userApi).rejectRegisterRequest(any(UserRegisterRejectVO.class));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> userController.rejectRegister(vo));
            assertEquals("该申请已被审批", ex.getMessage());
        }
    }

    // =================== GET /user/list ===================

    @Nested
    @DisplayName("getUserList")
    class GetUserList {

        @Test
        @DisplayName("返回用户列表")
        void return_user_list() {
            when(userApi.getUserList()).thenReturn(Arrays.asList("admin", "user1"));

            var result = userController.getUserList();

            assertEquals(2, result.size());
            assertTrue(result.contains("admin"));
            assertTrue(result.contains("user1"));
        }

        @Test
        @DisplayName("空用户列表")
        void return_empty_list() {
            when(userApi.getUserList()).thenReturn(Collections.emptyList());

            var result = userController.getUserList();

            assertTrue(result.isEmpty());
        }
    }
}
