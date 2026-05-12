---
name: user-registration-approval
overview: 实现用户注册审批功能：注册页面（含账号输入框）、审批列表/详情页面，后端新建审批表（完整审计记录），审批通过后创建正式用户。
design:
  styleKeywords:
    - 企业级
    - 简洁
    - 蓝色主调
  fontSystem:
    fontFamily: system-ui
    heading:
      size: 24px
      weight: 700
    subheading:
      size: 18px
      weight: 500
    body:
      size: 14px
      weight: 400
  colorSystem:
    primary:
      - "#0546CE"
    background:
      - "#FFFFFF"
      - "#F5F7FA"
    text:
      - "#303133"
      - "#606266"
    functional:
      - "#67C23A"
      - "#F56C6C"
      - "#E6A23C"
todos:
  - id: db-schema
    content: 新增数据库审批表和修改用户表
    status: completed
  - id: backend-entity
    content: 创建审批表实体、Mapper和枚举
    status: completed
    dependencies:
      - db-schema
  - id: backend-service
    content: 实现注册审批服务 RegisterService
    status: completed
    dependencies:
      - backend-entity
  - id: backend-controller
    content: 创建注册审批接口 Controller
    status: completed
    dependencies:
      - backend-service
  - id: frontend-api
    content: 新增前端用户相关API
    status: completed
  - id: login-page
    content: 改造登录页添加用户名输入框
    status: completed
  - id: register-page
    content: 创建用户注册页面
    status: completed
  - id: user-list-page
    content: 创建用户列表页面
    status: completed
  - id: approval-list-page
    content: 创建审批列表页面
    status: completed
  - id: router-i18n
    content: 配置路由和国际化
    status: completed
    dependencies:
      - register-page
      - user-list-page
      - approval-list-page
---

## 用户注册与审批功能需求

### 背景

ShowTime 监控系统需要增加用户注册功能，允许新用户申请账号，但需要经过现有用户审批后才能正式使用。

### 核心功能

#### 1. 用户注册

- 未登录用户可在登录页访问注册页面
- 填写用户名、密码后提交注册申请
- 注册申请进入审批流程，不自动创建正式用户

#### 2. 用户审批

- 所有已登录用户（包括 admin 和普通用户）均可查看待审批列表
- 审批人可以对申请进行"通过"或"拒绝"操作
- 拒绝时需填写拒绝原因
- 审批通过后正式创建用户账号

#### 3. 用户列表

- 所有已登录用户可查看已注册用户列表
- 显示用户名、注册时间、审批状态等信息

#### 4. 登录页改造

- 现有登录页仅需输入密码，用户名写死为 admin
- 需增加用户名输入框，改为标准登录界面
- 登录需验证用户状态：仅已审批用户可登录

### 数据设计

#### 审批表 (user_register_request)

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | VARCHAR(64) | 主键 |
| username | VARCHAR(100) | 申请用户名 |
| password | VARCHAR(255) | 密码(SM4加密) |
| status | VARCHAR(20) | 状态：pending/approved/rejected |
| submit_time | DATETIME | 提交时间 |
| approve_time | DATETIME | 审批时间 |
| approver | VARCHAR(100) | 审批人 |
| reject_reason | VARCHAR(500) | 拒绝原因 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |


#### 用户表扩展 (sys_user)

- 增加 `status` 字段：active(已激活)/inactive(未激活)
- admin 用户默认激活状态

## 技术栈

- 后端：Java 8 + Spring Boot 2.5.12 + MyBatis-Plus 3.5.3
- 前端：Vue 3 + TypeScript + Element Plus
- 数据库：MySQL

## 架构设计

### 后端模块

```
watcher-sdk/
├── entity/mysql/
│   └── UserRegisterRequest.java    # [NEW] 审批表实体
├── mapper/
│   └── UserRegisterRequestMapper.java  # [NEW] 审批表Mapper
├── dto/
│   ├── RegisterRequest.java        # [NEW] 注册请求DTO
│   ├── RegisterApproval.java      # [NEW] 审批请求DTO
│   └── UserListDTO.java           # [NEW] 用户列表DTO
└── constant/
    └── RegisterStatusEnum.java    # [NEW] 审批状态枚举

watcher-agent/
├── controller/
│   └── RegisterController.java    # [NEW] 注册与审批接口
└── service/
    └── RegisterService.java        # [NEW] 注册审批服务
```

### 前端模块

```
watcher-web/src/
├── api/
│   └── user/index.ts              # [MODIFY] 增加注册/审批API
├── views/main/
│   ├── user/                     # [NEW] 用户模块目录
│   │   ├── user-list.vue         # [NEW] 用户列表页
│   │   └── approval-list.vue     # [NEW] 审批列表页
│   └── register/                 # [NEW] 注册模块目录
│       └── index.vue             # [NEW] 注册页面
├── router/modules/
│   ├── user.ts                   # [NEW] 用户模块路由
│   └── register.ts               # [NEW] 注册模块路由
├── locale/modules/
│   ├── zh-cn/user.ts             # [NEW] 中文国际化
│   └── en/user.ts               # [NEW] 英文国际化
└── views/system/
    └── login.vue                 # [MODIFY] 登录页改造
```

### 接口设计

| 方法 | 路径 | 说明 | 权限 |
| --- | --- | --- | --- |
| POST | /user/register | 用户注册 | 无需登录 |
| GET | /user/register/pending | 获取待审批列表 | 已登录 |
| POST | /user/register/approve/{id} | 审批通过 | 已登录 |
| POST | /user/register/reject/{id} | 审批拒绝 | 已登录 |
| GET | /user/list | 用户列表 | 已登录 |


## 数据库脚本

- 在 schema-mysql.sql 中增加审批表
- 修改 sys_user 表增加 status 字段

## 设计风格

采用与现有项目一致的 Element Plus 组件库，保持蓝色主色调 (#0546CE) 的企业级设计风格。

## 页面设计

### 1. 登录页改造

- 在现有密码输入框上方增加用户名输入框
- 页面底部增加"没有账号？去注册"链接
- 保持原有布局和样式风格

### 2. 注册页面

- 简洁表单：用户名、密码、确认密码
- 提交后跳转登录页并提示"注册成功，请等待审批"
- 已有账号返回登录链接

### 3. 用户列表页

- 表格展示：用户名、注册时间、状态
- 支持分页
- 状态标签：已激活(绿色)、未激活(灰色)

### 4. 审批列表页

- 表格展示待审批申请：用户名、申请时间、操作
- 操作列：同意/拒绝按钮
- 拒绝弹窗需填写拒绝原因
- 已处理记录显示审批结果

# Agent Extensions

本需求不涉及额外的 Agent Extensions。