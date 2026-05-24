# ShowTime 项目长期记忆

## 管理员账号

| 属性 | 值 |
|------|-----|
| 用户名 | admin |
| 密码 | iesB4yJHVdE1R3mP4yT6LA== |

## 项目信息

- **项目路径**: `/Users/kirito/repos/ShowTime`
- **技术栈**: Java 17 + Vue 3 + TypeScript + Python FastAPI
- **前端端口**: 9090
- **后端端口**: 8888
- **RAG 服务端口**: 8000

## 数据库连接

- **MySQL**: jdbc:mariadb://localhost:3306/watcher_db
- **用户名**: root
- **密码**: root

## 服务地址

- 前端: http://localhost:9090
- 后端: http://localhost:8888/watcher
- RAG: http://localhost:8000

## 后端 API 鉴权

- 前端使用 `Authorization: Bearer <token>` header 传递 token
- 后端 `LoginInterceptor` 和 `ToolShareController` 已修改为支持此方式

## OpenCLI

- Adapter 路径: `~/.opencli/clis/showtime/`
- 工具分享测试命令: `opencli showtime tool-share-test --test all -f json`

## 工具分享页面 (tool-share)

- 路由: `/#/tool-share/index`
- 相关组件: `watcher-web/src/views/main/tool-share/index.vue`
- 相关接口:
  - GET `/api/tool-share/folders` - 获取文件夹列表
  - POST `/api/tool-share/folder` - 创建文件夹
  - GET `/api/tool-share/files` - 获取文件列表
  - DELETE `/api/tool-share/folder/{id}` - 删除文件夹
  - DELETE `/api/tool-share/file/{id}` - 删除文件

## 最后更新

2026-05-24