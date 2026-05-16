# Spec: Login Page Enhancement

## Scenarios

### Scenario 1: 登录页显示用户名输入框
- GIVEN: 用户打开登录页面 `/login`
- WHEN: 页面加载完成
- THEN: 用户名输入框可见且可输入
- THEN: 用户名输入框的 `placeholder` 为"请输入用户名"（国际化）
- THEN: 用户名输入框初始值为空

### Scenario 2: 登录表单校验 - 用户名为空
- GIVEN: 用户打开登录页面 `/login`
- WHEN: 用户未输入用户名，直接输入密码后点击登录
- THEN: 前端显示警告提示"用户名不能为空"
- THEN: 登录请求不发送

### Scenario 3: 登录表单校验 - 密码为空
- GIVEN: 用户打开登录页面 `/login`
- WHEN: 用户输入用户名但未输入密码，点击登录
- THEN: 前端显示警告提示"密码不能为空"
- THEN: 登录请求不发送

### Scenario 4: 登录成功跳转
- GIVEN: 用户已输入用户名和密码
- WHEN: 点击登录按钮，认证成功
- THEN: 页面跳转到首页 `/`
- THEN: 左侧导航显示知识库菜单项

### Scenario 5: 回车键触发表单提交
- GIVEN: 用户在用户名输入框中按下回车键
- WHEN: 用户名和密码均已填写
- THEN: 表单自动提交
- THEN: 执行登录流程