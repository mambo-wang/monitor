# Tasks

## Atomic TDD Task List

### Feature: 修复导航菜单图标显示

- [x] RED: 编写测试验证菜单图标显示
  - 测试文件：`watcher-web/tests/unit/menu.spec.ts`（如不存在则创建）
  - 测试内容：验证 `menu.ts` 中所有菜单项配置了 `meta.icon` 属性

- [x] GREEN: 补充菜单图标配置
  - 修改文件：`watcher-web/src/layout/Menu/menu.ts`
  - 实现描述：为所有菜单项补充缺失的 `meta.icon` 属性

### Feature: 添加工具分享页面国际化

- [x] RED: 编写测试验证工具分享按钮中文显示
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证 `$t('message.toolShare.createFolder')` 返回中文字符串

- [x] GREEN: 创建工具分享中文 locale 文件
  - 新建文件：`watcher-web/src/locale/modules/zh-cn/toolShare.ts`
  - 实现描述：添加 `message.toolShare` 的中文翻译（createFolder: "创建文件夹", uploadFile: "上传文件" 等）

- [x] GREEN: 创建工具分享英文 locale 文件
  - 新建文件：`watcher-web/src/locale/modules/en/toolShare.ts`
  - 实现描述：添加 `message.toolShare` 的英文翻译

- [x] GREEN: 导入 locale 模块到主文件
  - 修改文件：`watcher-web/src/locale/modules/zh-cn.ts` 和 `watcher-web/src/locale/modules/en.ts`
  - 实现描述：添加 `import toolShare from './zh-cn/toolShare'` 并合并到 message 对象

### Feature: 修复创建文件夹功能

- [x] RED: 编写测试验证创建文件夹 API 调用
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证 `createFolder` 方法正确调用 `createFolderApi`，parentId 为 null 时不传该字段

- [x] GREEN: 修复前端创建文件夹 API 调用
  - 修改文件：`watcher-web/src/views/main/tool-share/index.vue` 中的 `createFolder` 函数
  - 实现描述：当 `currentFolderId` 为 null 时，不将 `parentId` 传给 API

- [x] GREEN: 修复后端处理 parentId 为 null 的情况
  - 修改文件：`watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java`
  - 实现描述：在 `getFolders` 方法中，当 `parentId` 为 null 时查询根目录文件夹