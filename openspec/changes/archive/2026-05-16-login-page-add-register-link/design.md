# Design

## File Structure

### 修改文件

| 文件路径 | 操作 | 说明 |
|---------|------|------|
| `watcher-web/src/views/system/login.vue` | 修改 | 登录页增加"注册账号"链接 |
| `watcher-web/src/locale/modules/zh-cn/system.ts` | 修改 | 增加"注册账号"翻译 |
| `watcher-web/src/locale/modules/en/system.ts` | 修改 | 增加"注册账号"英文翻译 |

### 新增文件

| 文件路径 | 说明 |
|---------|------|
| `watcher-web/src/views/system/register.vue` | 用户注册页面 |
| `watcher-web/src/api/login/login.ts` | 注册 API 接口 |
| `watcher-web/tests/unit/register.spec.ts` | 注册页面单元测试 |

---

## Test Strategy

### `watcher-web/tests/unit/register.spec.ts`
- **测试策略**: 单元测试（Vitest + @vue/test-utils）
- **覆盖点**:
  - 注册页面可见"用户名"、"密码"、"确认密码"输入框
  - 表单校验：密码与确认密码不一致时报错
  - 注册提交成功后显示审批提示

### 测试运行命令
```bash
cd watcher-web && npm run test:unit
```

---

## 改动说明

### 1. 登录页 (`login.vue`)
- 在登录按钮下方增加"注册账号"链接
- 使用 `router.push('/register')` 跳转到注册页

### 2. 注册页面 (`register.vue`)
- 新页面，包含表单字段：用户名、密码、确认密码、手机（可选）
- 调用注册 API 提交申请
- 提交后显示"注册申请已提交，请等待管理员审批"提示

### 3. 注册 API
- 调用后端 `/api/user/register` 接口提交注册申请