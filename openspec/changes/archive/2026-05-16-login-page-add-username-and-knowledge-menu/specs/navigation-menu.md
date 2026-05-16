# Spec: Navigation Menu Cleanup

## Scenarios

### Scenario 1: 左侧导航不显示已删除的菜单项
- GIVEN: 用户已登录系统
- WHEN: 左侧导航菜单渲染
- THEN: 菜单中不显示"节点管理"（`/agent`）
- THEN: 菜单中不显示"租户信息认证"（`/tenant`）
- THEN: 菜单中不显示"网络参数管理"（`/net`）

### Scenario 2: 左侧导航显示知识库菜单
- GIVEN: 用户已登录系统
- WHEN: 左侧导航菜单渲染
- THEN: 菜单中显示"知识库"菜单项
- THEN: 知识库菜单图标为 `el-icon-document`

### Scenario 3: 知识库菜单可点击跳转
- GIVEN: 用户已登录系统
- WHEN: 用户点击知识库菜单
- THEN: 页面跳转到 `/knowledge`
- THEN: 知识库页面正常加载

### Scenario 4: 访问已删除菜单路径返回 404
- GIVEN: 用户已登录系统
- WHEN: 用户直接访问 `/agent` 路径
- THEN: 页面显示 404 或无权限访问
- THEN: 类似地，`/tenant` 和 `/net` 路径也无法正常访问

### Scenario 5: 知识库页面显示问答界面
- GIVEN: 用户访问 `/knowledge` 页面
- WHEN: 页面加载完成
- THEN: 显示问题输入框
- THEN: 显示历史记录列表区域