# Tasks

## Atomic TDD Task List

### Feature: 登录页增加注册入口

- [x] RED: 编写登录页注册入口测试——验证登录页显示"注册账号"链接
- [x] GREEN: 实现登录页注册入口——在 login.vue 登录按钮下方增加"注册账号"链接，点击跳转到 /register

---

### Feature: 创建注册页面

- [x] RED: 编写注册页面测试——验证 register.vue 包含用户名、密码、确认密码输入框
- [x] GREEN: 实现注册页面——创建 views/system/register.vue，包含表单字段和提交逻辑

---

### Feature: 注册表单校验

- [x] RED: 编写注册表单校验测试——验证密码与确认密码不一致时显示警告
- [x] GREEN: 实现注册表单校验——在 register.vue 中增加 checkForm 函数校验密码一致性

---

### Feature: 注册提交成功提示

- [x] RED: 编写注册成功提示测试——验证注册提交成功后显示审批提示
- [x] GREEN: 实现注册成功提示——注册成功后显示"注册申请已提交，请等待管理员审批"提示

---

### Feature: 注册 API 接口

- [x] GREEN: 实现注册 API——在 api/login/login.ts 中增加 registerApi 调用后端注册接口

---

### Feature: 国际化翻译

- [x] GREEN: 实现注册相关翻译——在 system.ts 中增加 registerLink、registerTitle、passwordMismatch 等翻译

---

### Feature: 注册页面单元测试

- [x] GREEN: 编写注册页面单元测试——创建 tests/unit/register.spec.ts

---

**任务完成顺序**: 登录页注册入口 → 注册页面 → 表单校验 → 成功提示 → API → 翻译 → 单元测试