# Tasks

## Atomic TDD Task List

### Feature: 左导航增加工具分享菜单

- [x] RED: 编写菜单测试——验证菜单列表包含 /tool-share 路径
- [x] GREEN: 实现菜单配置——在 menu.ts 中增加 toolShare 菜单配置

---

### Feature: 国际化翻译更新

- [x] RED: 编写国际化测试——验证 zh-cn/menu.ts 和 en/menu.ts 包含 toolShare 翻译
- [x] GREEN: 实现国际化更新——在 menu.ts 中增加 toolShare 翻译

---

### Feature: 创建工具分享路由

- [x] GREEN: 实现工具分享路由——在 router/modules 中增加 toolShare 路由配置

---

### Feature: 创建工具分享页面

- [x] RED: 编写工具分享页面测试——验证 index.vue 包含文件夹列表和文件列表组件
- [x] GREEN: 实现工具分享页面——创建 views/main/tool-share/index.vue，包含文件夹和文件展示

---

### Feature: 新建文件夹功能

- [x] RED: 编写新建文件夹测试——验证点击"新建文件夹"按钮显示对话框
- [x] GREEN: 实现新建文件夹——在页面中添加新建文件夹对话框和创建逻辑

---

### Feature: 上传文件功能

- [x] RED: 编写上传文件测试——验证上传对话框包含文件选择、工具名称、工具作用输入框
- [x] GREEN: 实现上传文件——添加上传对话框，支持选择文件和填写信息

---

### Feature: 下载文件统计下载量

- [x] RED: 编写下载测试——验证点击下载按钮调用下载 API
- [x] GREEN: 实现下载统计——添加下载按钮，调用 API 并增加下载量

---

### Feature: 工具分享 API 接口

- [x] GREEN: 实现 API——创建 api/tool-share/api.ts，包含文件列表、上传、下载、文件夹接口

---

### Feature: Java 后端 - 数据库表

- [x] GREEN: 创建数据库表——编写 tool_share_folder 和 tool_share_file 建表 SQL

---

### Feature: Java 后端 - 实体类

- [x] GREEN: 创建实体类——编写 ToolShareFolder.java 和 ToolShareFile.java

---

### Feature: Java后端 - Mapper 层

- [x] GREEN: 创建 Mapper——编写 ToolShareMapper.java 和 ToolShareMapper.xml

---

### Feature: Java 后端 - Service 层

- [x] RED: 编写 Service 测试——验证文件夹和文件 CRUD 操作
- [x] GREEN: 实现 Service——编写 ToolShareService.java 和 ToolShareServiceImpl.java

---

### Feature: Java 后端 - Controller 层

- [x] RED: 编写 Controller 测试——验证各 API 接口正常返回
- [x] GREEN: 实现 Controller——编写 ToolShareController.java，处理文件上传下载

---

### Feature: 工具分享页面单元测试

- [x] GREEN: 编写单元测试——创建 tests/unit/tool-share.spec.ts

---

### Feature: Java 后端单元测试

- [x] GREEN: 编写单元测试——创建 ToolShareServiceTest.java

---

**任务完成顺序**:
前端：菜单配置 → 国际化 → 路由 → 页面框架 → 新建文件夹 → 上传文件 → 下载统计 → API → 单元测试
后端：数据库表 → 实体类 → Mapper → Service → Controller → 单元测试