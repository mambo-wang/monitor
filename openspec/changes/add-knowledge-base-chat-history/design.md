# Design

## File Structure

### 1. API 层 (watcher-ai/src/watcher_ai/api/)

**新建文件：**
- `chat_history.py` - 对话历史 API 路由

### 2. Service 层 (watcher-ai/src/watcher_ai/services/)

**复用已有文件：**
- `chat_repository.py` - 已存在，数据库 CRUD 操作

### 3. 测试文件 (watcher-ai/tests/)

**新建文件：**
- `test_chat_history_api.py` - 对话历史 API 集成测试

### 4. 数据库脚本 (scripts/)

**新建文件：**
- `migrate_chat_history.sql` - 对话历史表结构

---

## Test Strategy

### test_chat_history_api.py
- **测试类型**: 集成测试（使用 FastAPI TestClient）
- **测试范围**:
  - 各 API 端点的正常路径测试
  - 权限校验测试（跨用户访问）
  - 边界条件测试（空列表、不存在的会话）
  - HTTP 状态码验证

### test_chat_repository.py
- **测试类型**: 单元测试（已存在，使用 Mock）
- **测试范围**: 已有覆盖数据库操作层

---

## 实现说明

### 数据库表设计

**chat_sessions 表：**
```sql
CREATE TABLE chat_sessions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(64) NOT NULL,
    kb_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) DEFAULT '',
    message_count INT DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_updated_at (updated_at)
);
```

**chat_messages 表：**
```sql
CREATE TABLE chat_messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    role VARCHAR(16) NOT NULL,
    content TEXT,
    sources JSON,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id)
);
```

### API 端点设计

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /api/knowledge/chat/history | 获取用户对话历史列表 |
| GET | /api/knowledge/chat/sessions/{session_id} | 获取会话详情及消息 |
| DELETE | /api/knowledge/chat/sessions/{session_id} | 删除对话会话 |
| POST | /api/knowledge/chat/with-history | 带历史的问答（保存记录） |

### 源码文件与测试文件的对应关系

| 源码文件 | 测试文件 |
|---------|---------|
| api/chat_history.py | tests/test_chat_history_api.py |
| services/chat_repository.py | tests/test_chat_repository.py |

### 测试运行命令

```bash
# 运行所有测试
cd watcher-ai && pytest tests/

# 运行 API 测试
cd watcher-ai && pytest tests/test_chat_history_api.py -v

# 运行 Repository 测试
cd watcher-ai && pytest tests/test_chat_repository.py -v
```
