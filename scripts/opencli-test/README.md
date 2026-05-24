# OpenCLI 工具分享页面测试

本目录包含工具分享页面的 OpenCLI 自动化测试脚本和 Adapter。

## 文件说明

| 文件 | 说明 |
|------|------|
| `tool-share-test.sh` | Shell 测试脚本 |
| `~/.opencli/clis/showtime/tool-share-test.js` | OpenCLI Adapter |

## 前置要求

1. 安装 OpenCLI: `npm install -g @jackwener/opencli`
2. 启动服务:
   - 前端: `http://localhost:9090`
   - 后端: `http://localhost:8888`
3. 确认 admin 账号密码: `admin / iesB4yJHVdE1R3mP4yT6LA==`

## 使用方法

### 方式一: Shell 脚本

```bash
# 运行全部测试
./tool-share-test.sh all

# 仅运行接口测试
./tool-share-test.sh api

# 仅运行浏览器测试
./tool-share-test.sh browser

# 指定测试类型
./tool-share-test.sh browser --test folders

# 清理测试数据
./tool-share-test.sh cleanup
```

### 方式二: OpenCLI Adapter

```bash
# 列出所有 showtime 相关命令
opencli list | grep showtime

# 运行 UI 测试
opencli showtime tool-share-test --test all -f json

# 仅测试文件夹
opencli showtime tool-share-test --test folders

# 仅测试按钮
opencli showtime tool-share-test --test buttons

# 仅测试导航
opencli showtime tool-share-test --test navigation
```

## 测试用例

### 接口测试
1. 获取文件夹列表 - GET `/api/tool-share/folders`
2. 创建新文件夹 - POST `/api/tool-share/folder`
3. 获取文件列表 - GET `/api/tool-share/files`

### 浏览器测试
| 测试用例 | 说明 |
|---------|------|
| `folder_list_loaded` | 验证文件夹列表加载 |
| `create_folder_button_exists` | 验证创建文件夹按钮存在 |
| `delete_folder_button_exists` | 验证删除文件夹按钮存在 |
| `folder_click_navigates` | 验证点击文件夹可以进入 |
| `button_texts_localized` | 验证按钮文本已翻译为中文 |

## 输出格式

```json
[
  {
    "test_case": "folder_list_loaded",
    "status": "PASS",
    "message": "Found 5 folders: mcp, 单测, ...",
    "timestamp": "2026-05-24T22:00:00.000Z"
  }
]
```

## 状态说明

| 状态 | 说明 |
|------|------|
| `PASS` | 测试通过 |
| `FAIL` | 测试失败 |
| `ERROR` | 执行错误 |