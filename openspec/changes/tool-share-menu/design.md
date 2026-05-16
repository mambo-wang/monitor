# Design

## File Structure

### 修改文件（前端）

| 文件路径 | 操作 | 说明 |
|---------|------|------|
| `watcher-web/src/layout/Menu/menu.ts` | 修改 | 增加"工具分享"菜单项 |
| `watcher-web/src/locale/modules/zh-cn/menu.ts` | 修改 | 增加 toolShare 翻译 |
| `watcher-web/src/locale/modules/en/menu.ts` | 修改 | 增加 toolShare 英文翻译 |

### 新增文件（前端）

| 文件路径 | 说明 |
|---------|------|
| `watcher-web/src/router/modules/toolShare.ts` | 工具分享路由配置 |
| `watcher-web/src/views/main/tool-share/index.vue` | 工具分享主页面 |
| `watcher-web/src/api/tool-share/api.ts` | 工具分享相关 API |
| `watcher-web/tests/unit/tool-share.spec.ts` | 工具分享页面单元测试 |

### 新增文件（Java 后端）

| 文件路径 | 说明 |
|---------|------|
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/controller/ToolShareController.java` | 工具分享 Controller |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/ToolShareService.java` | 工具分享 Service 接口 |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/service/impl/ToolShareServiceImpl.java` | 工具分享 Service 实现 |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/mapper/ToolShareMapper.java` | 工具分享 Mapper 接口 |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ToolShareFolder.java` | 文件夹实体 |
| `watcher-agent/src/main/java/com/virtual/cloud/om/agent/entity/ToolShareFile.java` | 文件实体 |
| `watcher-agent/src/main/resources/mapper/ToolShareMapper.xml` | MyBatis Mapper XML |
| `watcher-agent/src/main/resources/application.properties` | 添加文件存储路径配置 |

---

## Test Strategy

### 前端测试
### `watcher-web/tests/unit/tool-share.spec.ts`
- **测试策略**: 单元测试（Vitest + @vue/test-utils）
- **覆盖点**:
  - 页面加载显示文件夹列表和文件列表
  - 新建文件夹功能
  - 上传文件表单显示
  - 下载文件触发下载量统计

### 后端测试
### `watcher-agent/src/test/java/com/virtual/cloud/om/agent/service/ToolShareServiceTest.java`
- **测试策略**: 单元测试（JUnit 5 + Mockito）
- **覆盖点**:
  - 获取文件夹列表
  - 新建文件夹
  - 获取文件列表
  - 上传文件
  - 下载文件并增加下载量

### 测试运行命令
```bash
# 前端
cd watcher-web && npm run test:unit

# 后端
cd watcher-agent && mvn test -Dtest=ToolShareServiceTest
```

---

## 改动说明

### 1. 前端菜单配置 (`menu.ts`)
- 在 `/knowledge` 菜单后增加"工具分享"菜单：
  ```typescript
  {
    path: "/tool-share",
    redirect: "/tool-share/index",
    meta: { title: "message.menu.toolShare.name", icon: "el-icon-share" },
    hideMenu: false,
    children: [
      {
        path: "index",
        component: createNameComponent(() => import('@/views/main/tool-share/index.vue')),
        meta: { title: "message.menu.toolShare.name", icon: "el-icon-share", hideClose: true },
      },
    ],
  }
  ```

### 2. 工具分享页面 (`/tool-share/index.vue`)
- 左侧或上方显示文件夹列表
- 右侧显示文件列表（包含文件名、工具名称、工具作用、下载量、上传时间）
- 顶部有"新建文件夹"和"上传文件"按钮
- 上传文件弹窗包含：文件选择、工具名称输入、工具作用输入
- 下载按钮点击后调用下载 API 并增加下载量统计

### 3. Java 后端 Controller (`ToolShareController.java`)
```java
@RestController
@RequestMapping("/api/tool-share")
public class ToolShareController {

    @Resource
    private ToolShareService toolShareService;

    // 获取文件夹列表
    @GetMapping("/folders")
    public Result<List<ToolShareFolder>> getFolders(@RequestParam(required = false) Long parentId) {
        return Result.success(toolShareService.getFolders(parentId));
    }

    // 新建文件夹
    @PostMapping("/folder")
    public Result<Void> createFolder(@RequestBody ToolShareFolder folder) {
        toolShareService.createFolder(folder);
        return Result.success();
    }

    // 获取文件列表
    @GetMapping("/files")
    public Result<List<ToolShareFile>> getFiles(@RequestParam(required = false) Long folderId) {
        return Result.success(toolShareService.getFiles(folderId));
    }

    // 上传文件
    @PostMapping("/upload")
    public Result<ToolShareFile> uploadFile(@RequestParam("file") MultipartFile file,
                                            @RequestParam("toolName") String toolName,
                                            @RequestParam("toolDesc") String toolDesc,
                                            @RequestParam(required = false) Long folderId) {
        return Result.success(toolShareService.uploadFile(file, toolName, toolDesc, folderId));
    }

    // 下载文件（增加下载量）
    @GetMapping("/download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) {
        toolShareService.downloadFile(id, response);
    }
}
```

### 4. Java 后端实体

#### ToolShareFolder.java
```java
@Data
public class ToolShareFolder {
    private Long id;
    private String name;
    private Long parentId;
    private Long createUserId;
    private Date createTime;
}
```

#### ToolShareFile.java
```java
@Data
public class ToolShareFile {
    private Long id;
    private String fileName;        // 原始文件名
    private String toolName;        // 工具名称
    private String toolDesc;       // 工具作用描述
    private String filePath;       // 存储路径
    private Long fileSize;         // 文件大小
    private Long downloadCount;    // 下载量
    private Long folderId;         // 所属文件夹
    private Long createUserId;
    private Date createTime;
}
```

### 5. Service 接口设计
```java
public interface ToolShareService {
    List<ToolShareFolder> getFolders(Long parentId);
    void createFolder(ToolShareFolder folder);
    List<ToolShareFile> getFiles(Long folderId);
    ToolShareFile uploadFile(MultipartFile file, String toolName, String toolDesc, Long folderId);
    void downloadFile(Long id, HttpServletResponse response);
}
```

### 6. 数据库表设计（MySQL）

```sql
CREATE TABLE `tool_share_folder` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '文件夹名称',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父文件夹ID',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `tool_share_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `tool_name` VARCHAR(100) NOT NULL COMMENT '工具名称',
  `tool_desc` VARCHAR(500) DEFAULT NULL COMMENT '工具作用描述',
  `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_size` BIGINT NOT NULL COMMENT '文件大小',
  `download_count` BIGINT DEFAULT 0 COMMENT '下载量',
  `folder_id` BIGINT DEFAULT NULL COMMENT '所属文件夹ID',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL COMMENT '上传时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 7. API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/tool-share/folders` | 获取文件夹列表 |
| POST | `/api/tool-share/folder` | 新建文件夹 |
| GET | `/api/tool-share/files` | 获取文件列表 |
| POST | `/api/tool-share/upload` | 上传文件（ multipart/form-data） |
| GET | `/api/tool-share/download/{id}` | 下载文件（增加下载量） |

### 8. 文件存储配置
```properties
# application.properties
toolshare.upload.path=/data/toolshare/files
toolshare.max.file.size=104857600  # 100MB
```