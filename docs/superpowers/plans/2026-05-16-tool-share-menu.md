# 工具分享功能实现计划

**目标：** 在左导航增加"工具分享"菜单，支持上传下载工具、文件分类管理、下载量统计

**架构：** 前端 Vue 3 页面 + Java Spring Boot 后端，文件存储在本地，数据库存储元数据

**技术栈：** Vue 3 + TypeScript + Element Plus, Java Spring Boot + MyBatis-Plus, MySQL

---

## 文件结构

### 前端
- `watcher-web/src/layout/Menu/menu.ts` - 菜单配置
- `watcher-web/src/locale/modules/zh-cn/menu.ts` - 中文国际化
- `watcher-web/src/locale/modules/en/menu.ts` - 英文国际化
- `watcher-web/src/router/modules/toolShare.ts` - 路由配置
- `watcher-web/src/views/main/tool-share/index.vue` - 工具分享主页面
- `watcher-web/src/api/tool-share/api.ts` - API 接口

### Java 后端
- `entity/ToolShareFolder.java` - 文件夹实体
- `entity/ToolShareFile.java` - 文件实体
- `mapper/ToolShareFolderMapper.java` - 文件夹 Mapper
- `mapper/ToolShareFileMapper.java` - 文件 Mapper
- `mapper/ToolShareMapper.xml` - MyBatis XML
- `service/ToolShareService.java` - Service 接口
- `service/impl/ToolShareServiceImpl.java` - Service 实现
- `controller/ToolShareController.java` - REST Controller

### 数据库
- `scripts/init_tool_share.sql` - 建表 SQL

---

## 任务列表

<!-- openspec-task: F1 -->
### 任务 1：前端 - 菜单配置

**文件：** 修改 `watcher-web/src/layout/Menu/menu.ts`

- [x] 在 `/knowledge` 菜单后添加 toolShare 菜单配置
- [x] 图标使用 `el-icon-share`

---

<!-- openspec-task: F2 -->
### 任务 2：前端 - 国际化

**文件：** 修改 `zh-cn/menu.ts` 和 `en/menu.ts`

- [x] 添加 `toolShare: { name: '工具分享' }` 中文
- [x] 添加 `toolShare: { name: 'Tool Share' }` 英文

---

<!-- openspec-task: F3 -->
### 任务 3：前端 - 路由配置

**文件：** 创建 `watcher-web/src/router/modules/toolShare.ts`

- [x] 创建路由文件，路径 `/tool-share`
- [x] 在 `router/index.ts` 中引入并添加到 modules 数组

---

<!-- openspec-task: F4 -->
### 任务 4：前端 - 工具分享主页面

**文件：** 创建 `watcher-web/src/views/main/tool-share/index.vue`

- [x] 创建页面基本结构，包含文件夹列表和文件列表
- [x] 添加新建文件夹按钮和上传文件按钮
- [x] 添加返回上级目录功能

---

<!-- openspec-task: F5 -->
### 任务 5：前端 - 新建文件夹功能

**文件：** 修改 `index.vue`

- [x] 添加新建文件夹对话框
- [x] 实现 createFolderApi 调用

---

<!-- openspec-task: F6 -->
### 任务 6：前端 - 上传文件功能

**文件：** 修改 `index.vue`

- [x] 添加上传对话框，包含文件选择、工具名称、工具作用输入框
- [x] 实现 uploadFileApi 调用

---

<!-- openspec-task: F7 -->
### 任务 7：前端 - 下载文件功能

**文件：** 修改 `index.vue`

- [x] 添加下载按钮，点击调用 `/api/tool-share/download/{id}`
- [x] 文件列表显示下载量

---

<!-- openspec-task: F8 -->
### 任务 8：前端 - API 接口

**文件：** 创建 `watcher-web/src/api/tool-share/api.ts`

- [x] 实现 getFoldersApi、createFolderApi、getFilesApi、uploadFileApi、downloadFileApi

---

<!-- openspec-task: F9 -->
### 任务 9：数据库 - 建表 SQL

**文件：** 创建 `scripts/init_tool_share.sql`

- [x] 创建 `tool_share_folder` 表（id, name, parent_id, create_user_id, create_time）
- [x] 创建 `tool_share_file` 表（id, file_name, tool_name, tool_desc, file_path, file_size, download_count, folder_id, create_user_id, create_time）

---

<!-- openspec-task: F10 -->
### 任务 10：Java 实体类

**文件：** 创建 `entity/ToolShareFolder.java` 和 `entity/ToolShareFile.java`

- [x] ToolShareFolder: id, name, parentId, createUserId, createTime
- [x] ToolShareFile: id, fileName, toolName, toolDesc, filePath, fileSize, downloadCount, folderId, createUserId, createTime

---

<!-- openspec-task: F11 -->
### 任务 11：Java Mapper 层

**文件：** 创建 Mapper 接口和 XML

- [x] ToolShareFolderMapper.java (继承 BaseMapper)
- [x] ToolShareFileMapper.java (继承 BaseMapper)
- [x] ToolShareMapper.xml

---

<!-- openspec-task: F12 -->
### 任务 12：Java Service 层

**文件：** 创建 `service/ToolShareService.java` 和 `impl/ToolShareServiceImpl.java`

- [x] getFolders(Long parentId)
- [x] createFolder(ToolShareFolder folder)
- [x] getFiles(Long folderId)
- [x] uploadFile(MultipartFile file, String toolName, String toolDesc, Long folderId)
- [x] downloadFile(Long id, HttpServletResponse response) - 下载时增加 downloadCount

---

<!-- openspec-task: F13 -->
### 任务 13：Java Controller 层

**文件：** 创建 `controller/ToolShareController.java`

- [x] GET `/folders` - 获取文件夹列表
- [x] POST `/folder` - 新建文件夹
- [x] GET `/files` - 获取文件列表
- [x] POST `/upload` - 上传文件
- [x] GET `/download/{id}` - 下载文件（增加下载量）

---

**完成所有任务后运行：** `/opsx:archive tool-share-menu`