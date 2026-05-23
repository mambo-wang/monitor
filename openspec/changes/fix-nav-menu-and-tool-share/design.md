# Design

## File Structure

### 问题1：左导航菜单图标不显示

**源码文件：**
- `watcher-web/src/layout/Menu/menu.ts` - 菜单配置，缺少 icon 定义
- `watcher-web/src/locale/modules/zh-cn/menu.ts` - 中文菜单 locale（已正确配置 icon）
- `watcher-web/src/locale/modules/en/menu.ts` - 英文菜单 locale

**分析：**
菜单图标在 `MenuItem.vue` 中通过 `menu.meta.icon` 渲染，menu.ts 中部分菜单缺少 `meta.icon` 属性。

### 问题2：工具分享页面按钮名称不是中文

**源码文件：**
- `watcher-web/src/views/main/tool-share/index.vue` - 页面使用 `$t('message.toolShare.xxx')` 调用国际化
- `watcher-web/src/locale/modules/zh-cn/toolShare.ts` - **需要创建**
- `watcher-web/src/locale/modules/en/toolShare.ts` - **需要创建**
- `watcher-web/src/locale/modules/zh-cn.ts` - 需要导入 toolShare module
- `watcher-web/src/locale/modules/en.ts` - 需要导入 toolShare module

### 问题3：工具分享页面创建文件夹失败

**源码文件：**
- `watcher-web/src/api/tool-share/api.ts` - API 调用
- `watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/ToolShareController.java` - 后端 Controller
- `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java` - Service 实现
- `watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ToolShareFolder.java` - 实体类

**分析：**
前端调用 `createFolderApi({ name: xxx, parentId: xxx })` 时，parentId 传 null 后端会报错。需要修改前端在 parentId 为 null 时不传该字段，或后端处理 null 的 parentId。

**测试文件：**
- `watcher-web/tests/unit/tool-share.spec.ts` - 工具分享页面单元测试

## Test Strategy

### 工具分享页面测试 (`tests/unit/tool-share.spec.ts`)
- 测试创建文件夹功能
- 测试上传文件功能
- 测试按钮国际化显示

### 导航菜单测试
- 视觉验证所有菜单项显示图标
- 检查 menu.ts 配置是否完整