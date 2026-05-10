package com.virtual.cloud.om.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.sdk.api.UserApi;
import com.virtual.cloud.om.sdk.dto.UserRegisterRejectVO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestDetailDTO;
import com.virtual.cloud.om.sdk.dto.UserRegisterRequestVO;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import com.virtual.cloud.om.sdk.entity.mysql.UserRegisterRequest;
import com.virtual.cloud.om.sdk.mapper.SysUserMapper;
import com.virtual.cloud.om.sdk.mapper.UserRegisterRequestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * UserService 单元测试（TDD）
 *
 * TDD 流程：红灯 → 绿灯 → 重构
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 测试")
class UserServiceTest {

    @Mock
    private UserRegisterRequestMapper userRegisterRequestMapper;

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private UserService userService;

    private UserApi userApi;

    @BeforeEach
    void setUp() {
        userApi = userService;
    }

    @Nested
    @DisplayName("submitRegisterRequest - 提交注册申请")
    class SubmitRegisterRequestTests {

        @Test
        @DisplayName("正常提交申请成功")
        void should_submit_register_request_successfully() {
            // given: 用户名不存在，pending申请不存在
            when(sysUserMapper.selectByUsername("newuser")).thenReturn(null);
            when(userRegisterRequestMapper.selectByUsernameOrderByTimeDesc("newuser"))
                .thenReturn(Collections.emptyList());
            when(userRegisterRequestMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
            when(userRegisterRequestMapper.insert(any(UserRegisterRequest.class))).thenReturn(1);

            // when
            userApi.submitRegisterRequest("newuser", "password123", "申请备注");

            // then
            ArgumentCaptor<UserRegisterRequest> captor = ArgumentCaptor.forClass(UserRegisterRequest.class);
            verify(userRegisterRequestMapper).insert(captor.capture());
            UserRegisterRequest saved = captor.getValue();
            assertEquals("newuser", saved.getUsername());
            assertEquals("password123", saved.getPassword());
            assertEquals("申请备注", saved.getRemark());
            assertEquals("pending", saved.getStatus());
            assertEquals(0, saved.getRejectCount());
            assertNotNull(saved.getId());
        }

        @Test
        @DisplayName("用户名已存在于sys_user时抛出异常")
        void should_throw_when_username_exists_in_sys_user() {
            // given
            when(sysUserMapper.selectByUsername("existinguser")).thenReturn(new SysUser());

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userApi.submitRegisterRequest("existinguser", "password", null));
            assertEquals("用户名已存在", ex.getMessage());
            verify(userRegisterRequestMapper, never()).insert(any());
        }

        @Test
        @DisplayName("有待审批申请时抛出异常")
        void should_throw_when_pending_request_exists() {
            // given
            when(sysUserMapper.selectByUsername("user1")).thenReturn(null);
            when(userRegisterRequestMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userApi.submitRegisterRequest("user1", "password", null));
            assertEquals("该用户名有待审批的申请", ex.getMessage());
            verify(userRegisterRequestMapper, never()).insert(any());
        }

        @Test
        @DisplayName("历史被拒次数正确统计")
        void should_count_reject_history() {
            // given: 2条被拒历史
            when(sysUserMapper.selectByUsername("user2")).thenReturn(null);
            when(userRegisterRequestMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

            List<UserRegisterRequest> history = Arrays.asList(
                UserRegisterRequest.builder().status("rejected").build(),
                UserRegisterRequest.builder().status("rejected").build(),
                UserRegisterRequest.builder().status("approved").build()
            );
            when(userRegisterRequestMapper.selectByUsernameOrderByTimeDesc("user2")).thenReturn(history);
            when(userRegisterRequestMapper.insert(any(UserRegisterRequest.class))).thenReturn(1);

            // when
            userApi.submitRegisterRequest("user2", "password", null);

            // then
            ArgumentCaptor<UserRegisterRequest> captor = ArgumentCaptor.forClass(UserRegisterRequest.class);
            verify(userRegisterRequestMapper).insert(captor.capture());
            assertEquals(2, captor.getValue().getRejectCount());
        }
    }

    @Nested
    @DisplayName("getPendingRegisterRequests - 查询待审批列表")
    class GetPendingRequestsTests {

        @Test
        @DisplayName("返回pending状态的申请列表，按提交时间倒序")
        void should_return_pending_requests_ordered_by_submit_time() {
            // given
            UserRegisterRequest req1 = UserRegisterRequest.builder()
                .id("id1").username("user1").status("pending")
                .submitTime(LocalDateTime.of(2026, 5, 10, 10, 0))
                .rejectCount(0).build();
            UserRegisterRequest req2 = UserRegisterRequest.builder()
                .id("id2").username("user2").status("pending")
                .submitTime(LocalDateTime.of(2026, 5, 10, 11, 0))
                .rejectCount(1).build();
            when(userRegisterRequestMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Arrays.asList(req2, req1));

            // when
            List<UserRegisterRequestVO> result = userApi.getPendingRegisterRequests();

            // then
            assertEquals(2, result.size());
            assertEquals("id2", result.get(0).getId());
            assertEquals("id1", result.get(1).getId());
            verify(userRegisterRequestMapper).selectList(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("无待审批申请时返回空列表")
        void should_return_empty_list_when_no_pending_requests() {
            // given
            when(userRegisterRequestMapper.selectList(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.emptyList());

            // when
            List<UserRegisterRequestVO> result = userApi.getPendingRegisterRequests();

            // then
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getRegisterRequestDetail - 查询申请详情")
    class GetRegisterRequestDetailTests {

        @Test
        @DisplayName("返回申请详情并包含历史记录")
        void should_return_detail_with_history() {
            // given
            UserRegisterRequest current = UserRegisterRequest.builder()
                .id("id1").username("user1").status("pending")
                .remark("申请备注").rejectCount(2)
                .submitTime(LocalDateTime.of(2026, 5, 10, 10, 0))
                .build();
            when(userRegisterRequestMapper.selectById("id1")).thenReturn(current);
            List<UserRegisterRequest> history = Arrays.asList(current,
                UserRegisterRequest.builder().id("id0").username("user1")
                    .status("rejected").rejectReason("信息不全")
                    .submitTime(LocalDateTime.of(2026, 5, 9, 10, 0)).build());
            when(userRegisterRequestMapper.selectByUsernameOrderByTimeDesc("user1")).thenReturn(history);

            // when
            UserRegisterRequestDetailDTO detail = userApi.getRegisterRequestDetail("id1");

            // then
            assertEquals("id1", detail.getId());
            assertEquals("user1", detail.getUsername());
            assertEquals("pending", detail.getStatus());
            assertEquals(2, detail.getRejectCount());
            assertEquals(2, detail.getHistoryList().size());
        }

        @Test
        @DisplayName("申请记录不存在时抛出异常")
        void should_throw_when_request_not_found() {
            // given
            when(userRegisterRequestMapper.selectById("nonexist")).thenReturn(null);

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userApi.getRegisterRequestDetail("nonexist"));
            assertEquals("申请记录不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("approveRegisterRequest - 同意申请")
    class ApproveRegisterRequestTests {

        @Test
        @DisplayName("审批通过后自动创建sys_user记录")
        void should_create_sys_user_on_approve() {
            // given
            UserRegisterRequest request = UserRegisterRequest.builder()
                .id("id1").username("newuser").password("encrypted_pwd")
                .status("pending").submitTime(LocalDateTime.now()).build();
            when(userRegisterRequestMapper.selectById("id1")).thenReturn(request);
            when(sysUserMapper.insert(any(SysUser.class))).thenReturn(1);
            when(userRegisterRequestMapper.updateById(any(UserRegisterRequest.class))).thenReturn(1);

            // when
            userApi.approveRegisterRequest("id1");

            // then
            ArgumentCaptor<SysUser> userCaptor = ArgumentCaptor.forClass(SysUser.class);
            verify(sysUserMapper).insert(userCaptor.capture());
            SysUser createdUser = userCaptor.getValue();
            assertEquals("newuser", createdUser.getUsername());
            assertEquals("encrypted_pwd", createdUser.getPassword());
            assertEquals("active", createdUser.getStatus());

            ArgumentCaptor<UserRegisterRequest> reqCaptor = ArgumentCaptor.forClass(UserRegisterRequest.class);
            verify(userRegisterRequestMapper).updateById(reqCaptor.capture());
            assertEquals("approved", reqCaptor.getValue().getStatus());
        }

        @Test
        @DisplayName("申请已被审批时抛出异常")
        void should_throw_when_already_approved() {
            // given
            UserRegisterRequest request = UserRegisterRequest.builder()
                .id("id1").status("approved").build();
            when(userRegisterRequestMapper.selectById("id1")).thenReturn(request);

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userApi.approveRegisterRequest("id1"));
            assertEquals("该申请已被审批", ex.getMessage());
            verify(sysUserMapper, never()).insert(any());
        }

        @Test
        @DisplayName("申请记录不存在时抛出异常")
        void should_throw_when_request_not_found() {
            // given
            when(userRegisterRequestMapper.selectById("nonexist")).thenReturn(null);

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userApi.approveRegisterRequest("nonexist"));
            assertEquals("申请记录不存在", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("rejectRegisterRequest - 拒绝申请")
    class RejectRegisterRequestTests {

        @Test
        @DisplayName("拒绝申请并保存拒绝原因")
        void should_reject_with_reason() {
            // given
            UserRegisterRequest request = UserRegisterRequest.builder()
                .id("id1").username("user1").status("pending").build();
            when(userRegisterRequestMapper.selectById("id1")).thenReturn(request);
            when(userRegisterRequestMapper.updateById(any(UserRegisterRequest.class))).thenReturn(1);

            UserRegisterRejectVO vo = new UserRegisterRejectVO();
            vo.setId("id1");
            vo.setRejectReason("材料不全");

            // when
            userApi.rejectRegisterRequest(vo);

            // then
            ArgumentCaptor<UserRegisterRequest> captor = ArgumentCaptor.forClass(UserRegisterRequest.class);
            verify(userRegisterRequestMapper).updateById(captor.capture());
            assertEquals("rejected", captor.getValue().getStatus());
            assertEquals("材料不全", captor.getValue().getRejectReason());
        }

        @Test
        @DisplayName("拒绝原因可选，不填也能拒绝")
        void should_reject_without_reason() {
            // given
            UserRegisterRequest request = UserRegisterRequest.builder()
                .id("id1").status("pending").build();
            when(userRegisterRequestMapper.selectById("id1")).thenReturn(request);
            when(userRegisterRequestMapper.updateById(any(UserRegisterRequest.class))).thenReturn(1);

            UserRegisterRejectVO vo = new UserRegisterRejectVO();
            vo.setId("id1");

            // when
            userApi.rejectRegisterRequest(vo);

            // then
            ArgumentCaptor<UserRegisterRequest> captor = ArgumentCaptor.forClass(UserRegisterRequest.class);
            verify(userRegisterRequestMapper).updateById(captor.capture());
            assertEquals("rejected", captor.getValue().getStatus());
        }

        @Test
        @DisplayName("申请已被审批时抛出异常")
        void should_throw_when_already_approved() {
            // given
            UserRegisterRequest request = UserRegisterRequest.builder()
                .id("id1").status("approved").build();
            when(userRegisterRequestMapper.selectById("id1")).thenReturn(request);

            UserRegisterRejectVO vo = new UserRegisterRejectVO();
            vo.setId("id1");

            // when & then
            RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userApi.rejectRegisterRequest(vo));
            assertEquals("该申请已被审批", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("getUserList - 获取用户列表")
    class GetUserListTests {

        @Test
        @DisplayName("返回所有用户名列表")
        void should_return_all_usernames() {
            // given
            List<SysUser> users = Arrays.asList(
                SysUser.builder().id("1").username("admin").build(),
                SysUser.builder().id("2").username("user1").build()
            );
            when(sysUserMapper.selectList(any())).thenReturn(users);

            // when
            List<String> result = userApi.getUserList();

            // then
            assertEquals(2, result.size());
            assertTrue(result.contains("admin"));
            assertTrue(result.contains("user1"));
        }
    }
}
