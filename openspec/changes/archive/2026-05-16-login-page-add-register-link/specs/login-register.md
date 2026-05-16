# Spec: Login and Register

## Scenarios

### Scenario 1: 登录页显示注册入口
- GIVEN: 用户打开登录页面 `/login`
- WHEN: 页面加载完成
- THEN: 显示"注册账号"链接

### Scenario 2: 点击注册账号跳转到注册页
- GIVEN: 用户在登录页面
- WHEN: 点击"注册账号"链接
- THEN: 页面跳转到 `/register`
- THEN: 显示注册表单

### Scenario 3: 注册表单用户名输入
- GIVEN: 用户打开注册页面 `/register`
- WHEN: 页面加载完成
- THEN: 显示用户名输入框
- THEN: 显示密码输入框
- THEN: 显示确认密码输入框

### Scenario 4: 注册表单校验 - 密码不一致
- GIVEN: 用户在注册页面
- WHEN: 输入密码"123456"和确认密码"654321"
- THEN: 提交时显示"两次输入的密码不一致"警告

### Scenario 5: 注册成功显示审批提示
- GIVEN: 用户在注册页面填写了有效的用户名和密码
- WHEN: 点击"提交注册"按钮，注册成功
- THEN: 显示"注册申请已提交，请等待管理员审批"
- THEN: 页面不自动跳转登录