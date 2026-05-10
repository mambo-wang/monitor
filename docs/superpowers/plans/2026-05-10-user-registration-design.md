# 用户注册审批模块实现计划

> **面向 AI 代理的工作者：** 必需子技能：使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 逐任务实现此计划。步骤使用复选框（`- [ ]`）语法来跟踪进度。

**目标：** 实现用户注册申请功能，任意已激活用户可审批，包括注册页、审批页、用户列表页。

**架构：** 后端采用 Spring Boot 分层架构（Controller → Service → Mapper），前端采用 Vue 3 + TDesign 组件库。

**技术栈：** Spring Boot 2.5.12 / MyBatis-Plus 3.5.3 / Vue 3 / TDesign / MySQL

---

## 一、文件结构总览

### 后端（watcher-agent / watcher-sdk）

| 操作 | 文件路径 | 职责 |
|------|----------|------|
| 创建 | `watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/UserController.java` | 用户相关 REST 接口 |
| 创建 | `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/UserService.java` | 用户注册审批业务逻辑 |
| 修改 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/UserRegisterRequest.java` | 补充 rejectCount 字段 |
| 修改 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRequestDTO.java` | 补充 rejectCount 字段 |
| 创建 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRequestDetailDTO.java` | 申请详情 DTO（含历史） |
| 创建 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRequestVO.java` | 列表展示 VO |
| 创建 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRejectVO.java` | 拒绝申请 VO |
| 修改 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/UserRegisterRequestMapper.java` | 补充查询方法 |
| 修改 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/SysUserMapper.java` | 补充 selectByUsername |
| 创建 | `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/api/UserApi.java` | 用户 API 接口定义 |
| 修改 | `watcher-agent/src/main/resources/schema-mysql.sql` | 补充 reject_count 字段 |

### 前端（watcher-web）

| 操作 | 文件路径 | 职责 |
|------|----------|------|
| 创建 | `watcher-web/src/router/modules/user.ts` | 用户相关路由配置 |
| 创建 | `watcher-web/src/router/modules/register.ts` | 注册路由 |
| 修改 | `watcher-web/src/router/permission.ts` | 恢复白名单 |
| 修改 | `watcher-web/src/api/user.ts` | 补充注册审批 API |
| 创建 | `watcher-web/src/views/system/register.vue` | 用户注册页 |
| 创建 | `watcher-web/src/views/main/user/register-list.vue` | 注册申请列表页 |
| 创建 | `watcher-web/src/views/main/user/register-detail.vue` | 申请详情页（含审批操作） |
| 创建 | `watcher-web/src/views/main/user/user-list.vue` | 用户列表页 |
| 修改 | `watcher-web/src/locale/modules/zh-cn/menu.ts` | 补充菜单配置 |

---

## 二、里程碑划分

| 里程碑 | 内容 |
|--------|------|
| M1 | 数据库与后端实体准备 |
| M2 | 后端接口开发（Controller→Service→Mapper→Api） |
| M3 | 前端注册页 |
| M4 | 前端审批页 |
| M5 | 前端用户列表页 |

---

## 三、详细任务步骤

### M1：数据库与后端实体准备

---

#### 任务 1.1：补充数据库表字段

**文件：** `watcher-agent/src/main/resources/schema-mysql.sql`

- [ ] **步骤 1：在 `user_register_request` 表中增加 `reject_count` 字段**

在 `remark VARCHAR(500) COMMENT '备注',` 后添加：

```sql
    reject_count INT DEFAULT 0 COMMENT '累计被拒次数',
```

---

#### 任务 1.2：补充实体类字段

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/entity/mysql/UserRegisterRequest.java`

- [ ] **步骤 1：读取实体类内容**

- [ ] **步骤 2：在 `remark` 字段后添加**

```java
    @TableField("reject_count")
    private Integer rejectCount = 0;
```

---

#### 任务 1.3：补充 DTO 字段

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRequestDTO.java`

- [ ] **步骤 1：添加字段**

```java
    private Integer rejectCount = 0;
```

---

#### 任务 1.4：创建申请详情 DTO

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRequestDetailDTO.java`

- [ ] **步骤 1：创建文件**

```java
package com.virtual.cloud.om.sdk.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserRegisterRequestDetailDTO extends UserRegisterRequestDTO {
    private List<UserRegisterRequestDTO> historyList;
}
```

---

#### 任务 1.5：创建申请 VO

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRequestVO.java`

- [ ] **步骤 1：创建文件**

```java
package com.virtual.cloud.om.sdk.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@Data
@ApiModel("注册申请列表项VO")
public class UserRegisterRequestVO {
    @ApiModelProperty("申请ID")
    private String id;
    @ApiModelProperty("申请用户名")
    private String username;
    @ApiModelProperty("申请状态: pending/approved/rejected")
    private String status;
    @ApiModelProperty("提交时间")
    private String submitTime;
    @ApiModelProperty("审批人")
    private String approver;
    @ApiModelProperty("审批时间")
    private String approveTime;
    @ApiModelProperty("拒绝原因")
    private String rejectReason;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("累计被拒次数")
    private Integer rejectCount;
}
```

---

#### 任务 1.6：创建拒绝 VO

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/dto/UserRegisterRejectVO.java`

- [ ] **步骤 1：创建文件**

```java
package com.virtual.cloud.om.sdk.dto;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel("拒绝注册申请请求")
public class UserRegisterRejectVO {
    @ApiModelProperty("申请ID")
    @NotBlank(message = "申请ID不能为空")
    private String id;
    @ApiModelProperty("拒绝原因（可选）")
    private String rejectReason;
}
```

---

### M2：后端接口开发

---

#### 任务 2.1：创建 UserApi 接口

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/api/UserApi.java`

- [ ] **步骤 1：创建 API 接口**

```java
package com.virtual.cloud.om.sdk.api;

import com.virtual.cloud.om.sdk.dto.*;
import java.util.List;

public interface UserApi {
    /** 提交用户注册申请 */
    void submitRegisterRequest(String username, String password, String remark);
    /** 获取待审批注册申请列表 */
    List<UserRegisterRequestVO> getPendingRegisterRequests();
    /** 获取注册申请详情（含历史记录） */
    UserRegisterRequestDetailDTO getRegisterRequestDetail(String id);
    /** 同意注册申请 */
    void approveRegisterRequest(String id);
    /** 拒绝注册申请 */
    void rejectRegisterRequest(UserRegisterRejectVO vo);
    /** 获取用户列表（仅admin） */
    List<String> getUserList();
}
```

---

#### 任务 2.2：补充 Mapper 方法

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/UserRegisterRequestMapper.java`

- [ ] **步骤 1：读取现有 Mapper，补充方法**

```java
package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.UserRegisterRequest;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface UserRegisterRequestMapper extends BaseMapper<UserRegisterRequest> {

    @Select("SELECT * FROM user_register_request WHERE username = #{username} ORDER BY create_time DESC")
    List<UserRegisterRequest> selectByUsernameOrderByTimeDesc(@Param("username") String username);

}
```

**文件：** `watcher-sdk/src/main/java/com/virtual/cloud/om/sdk/mapper/SysUserMapper.java`

- [ ] **步骤 2：补充 selectByUsername 方法**

```java
package com.virtual.cloud.om.sdk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtual.cloud.om.sdk.entity.mysql.SysUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    @Select("SELECT * FROM sys_user WHERE username = #{username} LIMIT 1")
    SysUser selectByUsername(@Param("username") String username);
}
```

---

#### 任务 2.3：创建 UserService

**文件：** `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/UserService.java`

- [ ] **步骤 1：创建 Service 实现类**

```java
package com.virtual.cloud.om.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.virtual.cloud.om.sdk.api.UserApi;
import com.virtual.cloud.om.sdk.dto.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserApi {

    private final UserRegisterRequestMapper userRegisterRequestMapper;
    private final SysUserMapper sysUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRegisterRequest(String username, String password, String remark) {
        // 1. 校验用户名是否已存在
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
        int rejectCount = (int) history.stream().filter(r -> "rejected".equals(r.getStatus())).count();
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
        List<UserRegisterRequest> history = userRegisterRequestMapper.selectByUsernameOrderByTimeDesc(request.getUsername());
        List<UserRegisterRequestVO> historyVO = convertToVOList(history);
        UserRegisterRequestDetailDTO detail = new UserRegisterRequestDetailDTO();
        BeanUtils.copyProperties(request, detail);
        detail.setHistoryList(historyVO.stream().map(vo -> {
            UserRegisterRequestDTO dto = new UserRegisterRequestDTO();
            BeanUtils.copyProperties(vo, dto);
            return dto;
        }).collect(Collectors.toList()));
        return detail;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveRegisterRequest(String id) {
        UserRegisterRequest request = userRegisterRequestMapper.selectById(id);
        if (request == null) throw new RuntimeException("申请记录不存在");
        if (!"pending".equals(request.getStatus())) throw new RuntimeException("该申请已被审批");
        // 创建 sys_user
        SysUser user = new SysUser();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setStatus("active");
        sysUserMapper.insert(user);
        // 更新申请状态
        request.setStatus("approved");
        request.setApproveTime(LocalDateTime.now());
        userRegisterRequestMapper.updateById(request);
        log.info("[UserService] 审批通过用户 {} 的注册申请", request.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectRegisterRequest(UserRegisterRejectVO vo) {
        UserRegisterRequest request = userRegisterRequestMapper.selectById(vo.getId());
        if (request == null) throw new RuntimeException("申请记录不存在");
        if (!"pending".equals(request.getStatus())) throw new RuntimeException("该申请已被审批");
        request.setStatus("rejected");
        request.setRejectReason(vo.getRejectReason());
        request.setApproveTime(LocalDateTime.now());
        userRegisterRequestMapper.updateById(request);
        log.info("[UserService] 审批拒绝用户 {} 的注册申请", request.getUsername());
    }

    @Override
    public List<String> getUserList() {
        return sysUserMapper.selectList(null).stream().map(SysUser::getUsername).collect(Collectors.toList());
    }

    private List<UserRegisterRequestVO> convertToVOList(List<UserRegisterRequest> list) {
        List<UserRegisterRequestVO> voList = new ArrayList<>();
        for (UserRegisterRequest entity : list) {
            UserRegisterRequestVO vo = new UserRegisterRequestVO();
            BeanUtils.copyProperties(entity, vo);
            if (entity.getSubmitTime() != null) vo.setSubmitTime(entity.getSubmitTime().toString());
            if (entity.getApproveTime() != null) vo.setApproveTime(entity.getApproveTime().toString());
            voList.add(vo);
        }
        return voList;
    }
}
```

---

#### 任务 2.4：创建 UserController

**文件：** `watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/UserController.java`

- [ ] **步骤 1：创建 Controller**

```java
package com.virtual.cloud.om.agent.controller;

import com.virtual.cloud.om.sdk.api.UserApi;
import com.virtual.cloud.om.sdk.dto.*;
import com.virtual.cloud.om.sdk.vo.RpcResult;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserApi userApi;

    @ApiOperation(value = "提交注册申请")
    @PostMapping("/register")
    public RpcResult<Void> submitRegister(@RequestParam String username,
                                           @RequestParam String password,
                                           @RequestParam(required = false) String remark) {
        userApi.submitRegisterRequest(username, password, remark);
        return RpcResult.ok();
    }

    @ApiOperation(value = "查询待审批列表")
    @GetMapping("/register/pending")
    public RpcResult<List<UserRegisterRequestVO>> getPendingRequests() {
        return RpcResult.ok(userApi.getPendingRegisterRequests());
    }

    @ApiOperation(value = "查询申请详情（含历史）")
    @GetMapping("/register/{id}")
    public RpcResult<UserRegisterRequestDetailDTO> getRegisterDetail(@PathVariable String id) {
        return RpcResult.ok(userApi.getRegisterRequestDetail(id));
    }

    @ApiOperation(value = "同意注册申请")
    @PostMapping("/register/{id}/approve")
    public RpcResult<Void> approveRegister(@PathVariable String id) {
        userApi.approveRegisterRequest(id);
        return RpcResult.ok();
    }

    @ApiOperation(value = "拒绝注册申请")
    @PostMapping("/register/reject")
    public RpcResult<Void> rejectRegister(@RequestBody @Valid UserRegisterRejectVO vo) {
        userApi.rejectRegisterRequest(vo);
        return RpcResult.ok();
    }

    @ApiOperation(value = "获取用户列表")
    @GetMapping("/list")
    public RpcResult<List<String>> getUserList() {
        return RpcResult.ok(userApi.getUserList());
    }
}
```

---

### M3：前端注册页

---

#### 任务 3.1：创建注册路由

**文件：** `watcher-web/src/router/modules/register.ts`

- [ ] **步骤 1：创建注册路由模块**

```typescript
import type { RouteConfig } from 'vue-router';

const routes: RouteConfig[] = [
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/system/register.vue'),
    meta: { title: '注册', public: true },
  },
];

export default routes;
```

---

#### 任务 3.2：恢复白名单

**文件：** `watcher-web/src/router/permission.ts`

- [ ] **步骤 1：读取 `permission.ts`**

- [ ] **步骤 2：在 whiteList 中添加 `/register`**

```typescript
const whiteList = ['/login', '/register'];
```

---

#### 任务 3.3：创建注册页面

**文件：** `watcher-web/src/views/system/register.vue`

- [ ] **步骤 1：读取 `login.vue` 作为模板参考**

- [ ] **步骤 2：创建注册页面**

```vue
<script setup lang="ts">
import { ref } from 'vue';
import { Message } from 'tdesign-vue-next';
import { register } from '@/api/user';

const formData = ref({
  username: '',
  password: '',
  confirmPassword: '',
  remark: '',
});
const loading = ref(false);

const handleRegister = async () => {
  if (!formData.value.username) { Message.error('请输入用户名'); return; }
  if (!formData.value.password) { Message.error('请输入密码'); return; }
  if (formData.value.password !== formData.value.confirmPassword) {
    Message.error('两次密码输入不一致'); return;
  }
  loading.value = true;
  try {
    await register({
      username: formData.value.username,
      password: formData.value.password,
      remark: formData.value.remark,
    });
    Message.success('注册申请已提交，请等待审批');
    window.location.href = '/login';
  } catch (e: any) {
    Message.error(e?.message || '注册失败');
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="title">用户注册</h2>
      <t-form :data="formData" @submit="handleRegister">
        <t-form-item name="username" label="用户名">
          <t-input v-model="formData.username" placeholder="请输入用户名" />
        </t-form-item>
        <t-form-item name="password" label="密码">
          <t-input v-model="formData.password" type="password" placeholder="请输入密码" />
        </t-form-item>
        <t-form-item name="confirmPassword" label="确认密码">
          <t-input v-model="formData.confirmPassword" type="password" placeholder="请再次输入密码" />
        </t-form-item>
        <t-form-item name="remark" label="备注">
          <t-input v-model="formData.remark" placeholder="可选，填写申请说明" />
        </t-form-item>
        <t-form-item>
          <t-button type="submit" theme="primary" block :loading="loading">提交注册申请</t-button>
        </t-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
        </div>
      </t-form>
    </div>
  </div>
</template>

<style scoped>
.register-container {
  display: flex; justify-content: center; align-items: center; min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.register-box {
  background: #fff; padding: 40px; border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1); width: 400px;
}
.title { text-align: center; margin-bottom: 24px; color: #333; }
.login-link { text-align: center; margin-top: 16px; color: #666; }
.login-link a { color: #0052d9; text-decoration: none; }
</style>
```

---

#### 任务 3.4：补充 API 调用

**文件：** `watcher-web/src/api/user.ts`

- [ ] **步骤 1：读取现有 API 文件**

- [ ] **步骤 2：追加注册相关接口**

```typescript
export interface RegisterRequest { username: string; password: string; remark?: string; }

export interface RegisterDetailVO {
  id: string; username: string; status: 'pending' | 'approved' | 'rejected';
  submitTime: string; approver: string; approveTime: string;
  rejectReason: string; remark: string; rejectCount: number;
  historyList: RegisterDetailVO[];
}

export const register = (data: RegisterRequest) => {
  return request.post('/user/register', null, { params: data });
};

export const getPendingRegisterList = () => {
  return request.get<BaseResponse<RegisterDetailVO[]>>('/user/register/pending');
};

export const getRegisterDetail = (id: string) => {
  return request.get<BaseResponse<RegisterDetailVO>>(`/user/register/${id}`);
};

export const approveRegister = (id: string) => {
  return request.post(`/user/register/${id}/approve`);
};

export const rejectRegister = (data: { id: string; rejectReason?: string }) => {
  return request.post('/user/register/reject', data);
};
```

---

### M4：前端审批页

---

#### 任务 4.1：创建注册申请列表页

**文件：** `watcher-web/src/views/main/user/register-list.vue`

- [ ] **步骤 1：创建列表页**

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Message } from 'tdesign-vue-next';
import { getPendingRegisterList } from '@/api/user';
import type { RegisterDetailVO } from '@/api/user';

const router = useRouter();
const loading = ref(false);
const registerList = ref<RegisterDetailVO[]>([]);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getPendingRegisterList();
    registerList.value = res.data || [];
  } catch (e: any) { Message.error(e?.message || '加载失败'); }
  finally { loading.value = false; }
};

const goDetail = (row: RegisterDetailVO) => {
  router.push({ path: '/main/user/register-detail', query: { id: row.id } });
};

onMounted(() => { loadData(); });
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>注册申请审批</h2></div>
    <t-table :data="registerList" :loading="loading" row-key="id" hover stripe @row-click="goDetail">
      <t-table-column title="用户名" key="username" />
      <t-table-column title="备注" key="remark" />
      <t-table-column title="被拒次数" key="rejectCount" width="100" />
      <t-table-column title="提交时间" key="submitTime" />
      <t-table-column title="操作" width="120">
        <template #default="{ row }">
          <t-button size="small" variant="text" @click.stop="goDetail(row)">查看详情</t-button>
        </template>
      </t-table-column>
    </t-table>
  </div>
</template>

<style scoped>
.page-container { padding: 24px; }
.page-header { margin-bottom: 24px; }
</style>
```

---

#### 任务 4.2：创建注册申请详情页

**文件：** `watcher-web/src/views/main/user/register-detail.vue`

- [ ] **步骤 1：创建详情页（含审批操作）**

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { Message } from 'tdesign-vue-next';
import { getRegisterDetail, approveRegister, rejectRegister } from '@/api/user';
import type { RegisterDetailVO } from '@/api/user';

const route = useRoute();
const loading = ref(false);
const detail = ref<RegisterDetailVO | null>(null);
const rejectDialogVisible = ref(false);
const rejectReason = ref('');
const actionLoading = ref(false);

const loadDetail = async () => {
  const id = route.query.id as string;
  if (!id) return;
  loading.value = true;
  try {
    const res = await getRegisterDetail(id);
    detail.value = res.data;
  } catch (e: any) { Message.error(e?.message || '加载失败'); }
  finally { loading.value = false; }
};

const handleApprove = async () => {
  if (!detail.value) return;
  actionLoading.value = true;
  try {
    await approveRegister(detail.value.id);
    Message.success('已同意该申请');
    await loadDetail();
  } catch (e: any) { Message.error(e?.message || '操作失败'); }
  finally { actionLoading.value = false; }
};

const openRejectDialog = () => {
  rejectReason.value = '';
  rejectDialogVisible.value = true;
};

const handleReject = async () => {
  if (!detail.value) return;
  actionLoading.value = true;
  try {
    await rejectRegister({ id: detail.value.id, rejectReason: rejectReason.value });
    Message.success('已拒绝该申请');
    rejectDialogVisible.value = false;
    await loadDetail();
  } catch (e: any) { Message.error(e?.message || '操作失败'); }
  finally { actionLoading.value = false; }
};

onMounted(() => { loadDetail(); });
</script>

<template>
  <div class="page-container">
    <t-loading :loading="loading">
      <div v-if="detail">
        <div class="info-card">
          <h3>申请信息</h3>
          <t-descriptions :column="2" border>
            <t-descriptions-item label="用户名">{{ detail.username }}</t-descriptions-item>
            <t-descriptions-item label="状态">
              <t-tag :theme="detail.status === 'pending' ? 'warning' : detail.status === 'approved' ? 'success' : 'danger'">
                {{ detail.status === 'pending' ? '待审批' : detail.status === 'approved' ? '已通过' : '已拒绝' }}
              </t-tag>
            </t-descriptions-item>
            <t-descriptions-item label="备注" :span="2">{{ detail.remark || '-' }}</t-descriptions-item>
            <t-descriptions-item label="提交时间">{{ detail.submitTime }}</t-descriptions-item>
            <t-descriptions-item label="累计被拒次数">{{ detail.rejectCount }}</t-descriptions-item>
          </t-descriptions>
        </div>

        <div v-if="detail.status === 'pending'" class="action-bar">
          <t-button theme="success" :loading="actionLoading" @click="handleApprove">同意</t-button>
          <t-button theme="danger" :loading="actionLoading" @click="openRejectDialog">拒绝</t-button>
        </div>

        <div v-if="detail.status === 'rejected'" class="info-card">
          <h3>拒绝原因</h3>
          <p>{{ detail.rejectReason || '无' }}</p>
        </div>

        <div class="info-card">
          <h3>历史申请记录</h3>
          <t-table :data="detail.historyList" :columns="[
            { colKey: 'submitTime', title: '提交时间' },
            { colKey: 'status', title: '状态' },
            { colKey: 'rejectReason', title: '拒绝原因' },
            { colKey: 'approveTime', title: '审批时间' },
          ]" row-key="id" />
        </div>
      </div>
    </t-loading>

    <t-dialog v-model:visible="rejectDialogVisible" header="拒绝申请" :on-confirm="handleReject" :confirm-loading="actionLoading">
      <t-form label-width="80">
        <t-form-item label="拒绝原因">
          <t-textarea v-model="rejectReason" placeholder="可选，填写拒绝原因" />
        </t-form-item>
      </t-form>
    </t-dialog>
  </div>
</template>

<style scoped>
.page-container { padding: 24px; }
.info-card { background: #fff; border-radius: 8px; padding: 24px; margin-bottom: 24px; }
.info-card h3 { margin-bottom: 16px; color: #333; }
.action-bar { display: flex; gap: 12px; margin-bottom: 24px; }
</style>
```

---

### M5：前端用户列表页

---

#### 任务 5.1：创建用户列表页

**文件：** `watcher-web/src/views/main/user/user-list.vue`

- [ ] **步骤 1：创建用户列表页**

```vue
<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { Message } from 'tdesign-vue-next';
import { getUserList } from '@/api/user';

const loading = ref(false);
const userList = ref<string[]>([]);

const loadData = async () => {
  loading.value = true;
  try {
    const res = await getUserList();
    userList.value = res.data || [];
  } catch (e: any) { Message.error(e?.message || '加载失败'); }
  finally { loading.value = false; }
};

onMounted(() => { loadData(); });
</script>

<template>
  <div class="page-container">
    <div class="page-header"><h2>用户列表</h2></div>
    <t-table :data="userList.map((name, i) => ({ index: i + 1, username: name }))"
      :loading="loading" row-key="username" hover stripe>
      <t-table-column title="序号" key="index" width="80" />
      <t-table-column title="用户名" key="username" />
    </t-table>
  </div>
</template>

<style scoped>
.page-container { padding: 24px; }
.page-header { margin-bottom: 24px; }
</style>
```

---

#### 任务 5.2：配置用户路由模块

**文件：** `watcher-web/src/router/modules/user.ts`

- [ ] **步骤 1：创建用户路由模块**

```typescript
import type { RouteConfig } from 'vue-router';

const routes: RouteConfig[] = [
  {
    path: '/main/user/register-list',
    name: 'RegisterList',
    component: () => import('@/views/main/user/register-list.vue'),
    meta: { title: '注册审批', icon: 'check-circle' },
  },
  {
    path: '/main/user/register-detail',
    name: 'RegisterDetail',
    component: () => import('@/views/main/user/register-detail.vue'),
    meta: { title: '申请详情' },
  },
  {
    path: '/main/user/user-list',
    name: 'UserList',
    component: () => import('@/views/main/user/user-list.vue'),
    meta: { title: '用户列表', icon: 'user' },
  },
];

export default routes;
```

---

#### 任务 5.3：补充菜单配置

**文件：** `watcher-web/src/locale/modules/zh-cn/menu.ts`

- [ ] **步骤 1：读取现有菜单配置**

- [ ] **步骤 2：在用户管理菜单项下补充子菜单**

```typescript
user: {
  title: '用户管理',
  children: {
    'register-list': '注册审批',
    'user-list': '用户列表',
  },
},
```

---

## 四、验收标准

- [ ] 数据库 `user_register_request` 表包含 `reject_count` 字段
- [ ] 后端可正常启动，Controller 接口正常响应
- [ ] 注册页面可提交申请，提示"注册申请已提交"
- [ ] 登录用户可查看待审批列表
- [ ] 审批详情页展示申请信息 + 历史记录
- [ ] 同意操作后自动创建用户，用户可登录
- [ ] 拒绝操作后申请人可重新提交
- [ ] 用户列表页面展示所有已激活用户
