# Tasks

## Atomic TDD Task List

### Feature: 显示顶级文件夹

- [x] RED: 编写测试验证文件夹列表只显示顶级文件夹
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证 `getFolders()` 只返回 `parentId` 为 null 的文件夹

- [x] GREEN: 修改前端页面只显示顶级文件夹
  - 修改文件：`watcher-web/src/views/main/tool-share/index.vue`
  - 实现描述：修改 `loadFolders` 方法，传递 `parentId: null` 只获取顶级文件夹

### Feature: 点击文件夹显示文件列表

- [x] RED: 编写测试验证点击文件夹后显示文件列表
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证点击文件夹后右侧显示该文件夹内的文件

- [x] GREEN: 修改前端点击文件夹时加载文件
  - 修改文件：`watcher-web/src/views/main/tool-share/index.vue`
  - 实现描述：点击文件夹时设置 `currentFolderId` 并调用 `loadFiles()`

### Feature: 文件按下载量排序

- [x] RED: 编写测试验证文件按 downloadCount 降序
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证文件列表排序

- [x] GREEN: 修改后端文件查询排序
  - 修改文件：`watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java`
  - 实现描述：修改 `getFiles()` 方法，添加 `.orderByDesc(ToolShareFile::getDownloadCount)`

### Feature: 删除文件夹功能

- [x] RED: 编写测试验证删除文件夹 API
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证 `deleteFolderApi` 正确调用

- [x] GREEN: 添加后端删除文件夹接口
  - 修改文件：`watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/ToolShareController.java`
  - 新增方法：`DELETE /api/tool-share/folder/{id}`

- [x] GREEN: 实现删除文件夹服务方法
  - 修改文件：`watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java`
  - 实现描述：添加 `deleteFolder()` 方法，删除前检查是否有子文件

### Feature: 删除文件功能

- [x] RED: 编写测试验证删除文件 API
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证 `deleteFileApi` 正确调用

- [x] GREEN: 添加后端删除文件接口
  - 修改文件：`watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/ToolShareController.java`
  - 新增方法：`DELETE /api/tool-share/file/{id}`

- [x] GREEN: 实现删除文件服务方法
  - 修改文件：`watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java`
  - 实现描述：添加 `deleteFile()` 方法，删除文件记录和物理文件

### Feature: 前端删除按钮和确认对话框

- [x] RED: 编写测试验证删除按钮和确认对话框
  - 测试文件：`watcher-web/tests/unit/tool-share.spec.ts`
  - 测试内容：验证删除按钮可见，点击后显示确认对话框

- [x] GREEN: 添加前端删除按钮和确认对话框
  - 修改文件：`watcher-web/src/views/main/tool-share/index.vue`
  - 实现描述：在文件夹和文件旁边添加删除按钮，点击后显示 `ElMessageBox.confirm`