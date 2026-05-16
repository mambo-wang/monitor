# Tasks

## Atomic TDD Task List

### Feature: 登录页增加用户名输入框

- [x] RED: 编写登录页用户名输入框测试——验证用户名输入框可见，初始值为空字符串
- [x] GREEN: 实现登录页用户名输入框——在 login.vue 中增加 el-input 组件，form.username 初始值设为空

---

### Feature: 登录表单用户名校验

- [x] RED: 编写用户名非空校验测试——验证用户名为空时点击登录显示警告提示
- [x] GREEN: 实现用户名非空校验——在 checkForm 函数中增加用户名非空判断

---

### Feature: 删除左导航菜单项

- [x] RED: 编写菜单配置测试——验证菜单列表不包含 /agent、/tenant、/net 路径
- [x] GREEN: 实现菜单配置删除——从 menu.ts 中删除 agent、tenant、net 三个菜单块

---

### Feature: 左导航新增知识库菜单

- [x] RED: 编写知识库菜单测试——验证菜单列表包含 /knowledge 路径，meta.title 为 message.menu.knowledge.name
- [x] GREEN: 实现知识库菜单——在 menu.ts 中增加 knowledge 菜单配置

---

### Feature: 国际化翻译更新

- [x] RED: 编写国际化测试——验证 zh-cn/menu.ts 和 en/menu.ts 包含 knowledge 翻译，不包含 agent/tenant/net 翻译
- [x] GREEN: 实现国际化更新——在 menu.ts 中增加 knowledge 翻译，删除 agent、tenant、net 翻译

---

### Feature: 创建知识库页面

- [x] RED: 编写知识库页面测试——验证 /knowledge 路由指向的组件存在并渲染问答界面
- [x] GREEN: 实现知识库页面——创建 views/main/knowledge/index.vue，包含输入框和历史记录展示

---

### Feature: 登录页单元测试

- [x] GREEN: 编写登录页单元测试——创建 tests/unit/login.spec.ts，测试用户名输入框和表单校验
- [x] GREEN: 编写菜单单元测试——创建 tests/unit/menu.spec.ts，测试菜单配置正确性

---

**任务完成顺序**: 登录页用户名 → 登录校验 → 菜单删除 → 菜单新增 → 国际化 → 知识库页面 → 单元测试