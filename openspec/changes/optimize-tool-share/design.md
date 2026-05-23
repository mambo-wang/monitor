# Design

## File Structure

### 前端文件
| 文件路径 | 说明 |
|---------|------|
| `watcher-web/src/views/main/tool-share/index.vue` | 修改：点击文件夹显示文件列表，添加删除按钮 |
| `watcher-web/src/api/tool-share/api.ts` | 添加删除文件夹和文件的 API |

### 后端文件
| 文件路径 | 说明 |
|---------|------|
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/ToolShareController.java` | 添加删除接口 |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/ToolShareService.java` | 添加删除方法声明 |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java` | 实现删除方法 |

### 测试文件
| 文件路径 | 说明 |
|---------|------|
| `watcher-web/tests/unit/tool-share.spec.ts` | 添加删除功能测试 |

## Test Strategy

### 前端单元测试 (`tests/unit/tool-share.spec.ts`)
- 测试文件夹点击后显示文件列表
- 测试删除按钮显示
- 测试删除确认对话框
- 测试文件按下载量排序

### 后端集成测试
- 测试删除文件夹接口
- 测试删除文件接口
- 测试删除有文件的文件夹返回错误
- 测试文件按 downloadCount 降序排列

## API 设计

### 删除文件夹
```
DELETE /api/tool-share/folder/{id}
```
返回：`{ success: true }` 或 `{ success: false, message: "xxx" }`

### 删除文件
```
DELETE /api/tool-share/file/{id}
```
返回：`{ success: true }` 或 `{ success: false, message: "xxx" }`

### 获取文件列表（已存在，需修改排序）
```
GET /api/tool-share/files?folderId=xxx
```
返回：`{ success: true, data: [...] }`

文件列表按 `downloadCount` 降序排列

## 页面交互

1. **左侧文件夹面板**：只显示顶级文件夹（parentId 为 null）
2. **点击文件夹**：右侧显示该文件夹内的文件，文件按下载量降序
3. **删除按钮**：每个文件夹和文件旁边显示删除图标
4. **确认对话框**：点击删除后弹出确认对话框"