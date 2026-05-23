# Spec - Navigation Menu Icons

## Scenarios

### Scenario 1: 左导航菜单显示图标
- GIVEN: 用户已登录系统并进入主页面
- WHEN: 用户查看左侧导航菜单
- THEN: 每个菜单项左侧显示对应的图标（Dashboard 显示 el-icon-monitor，资源列表显示 el-icon-box 等）

### Scenario 2: 导航菜单收起时图标可见
- GIVEN: 用户点击导航菜单的折叠按钮
- WHEN: 导航菜单收起为折叠状态
- THEN: 折叠后的菜单项仍然显示图标

### Scenario 3: 子菜单显示展开/收起图标
- GIVEN: 用户查看有子菜单的导航项（如系统目录）
- WHEN: 子菜单收起时
- THEN: 显示展开箭头图标
- AND: 当子菜单展开后显示收起箭头图标