# ShowTime SKILL

## 触发条件

当用户说以下内容时触发：
- "查看资源"、"列出资源"、"查询资源"、"资源详情"
- "查看指标"、"指标类型"、"指标趋势"、"指标汇总"、"最新指标"、"监控数据"
- "使用知识库问答"、"在知识库里问一个问题"、"RAG 问答"、"查询知识库"、"帮我问下知识库"
- "知识库会话"、"知识库 Chat"、"列出知识库"、"查看会话"、"删除会话"

## 功能描述

此 Skill 用于通过 MCP 协议调用 ShowTime 监控系统，提供资源管理、指标监控和 RAG 知识库问答功能。

## MCP 服务配置

CodeBuddy MCP 配置名称：`showtime-rag`
MCP 服务地址：`http://localhost:7777`（HTTP 模式）或 stdio 模式

## 可用 MCP 工具

### 资源管理工具

#### showtime_list_resources

列出 ShowTime 中的所有资源，支持按平台、名称、IP 地址过滤。

| 参数 | 类型 | 说明 |
|------|------|------|
| platform | string | 平台类型：cas, workspace, uis, onestor |
| resource_name | string | 资源名称（模糊匹配） |
| ip_address | string | IP 地址 |
| limit | int | 返回数量，默认 100 |
| offset | int | 跳过数量，默认 0 |

**调用示例**：
```
用户：列出所有 CAS 资源
Skill：调用 showtime_list_resources（platform="cas"）

用户：查找 IP 为 192.168.1.100 的资源
Skill：调用 showtime_list_resources（ip_address="192.168.1.100"）
```

---

#### showtime_get_resource_detail

获取指定资源的详细信息。

| 参数 | 类型 | 说明 |
|------|------|------|
| resource_id | string | 资源 ID（必填） |

**调用示例**：
```
用户：查看资源 res001 的详情
Skill：调用 showtime_get_resource_detail（resource_id="res001"）
```

---

### 指标监控工具

#### showtime_list_metric_types

列出所有可用的指标类型。

| 参数 | 类型 | 说明 |
|------|------|------|
| platform | string | 平台类型（可选） |

**调用示例**：
```
用户：列出所有指标类型
Skill：调用 showtime_list_metric_types（）

用户：查看 CAS 平台的指标类型
Skill：调用 showtime_list_metric_types（platform="cas"）
```

---

#### showtime_get_metric_trend

获取资源指标的历史趋势数据。

| 参数 | 类型 | 说明 |
|------|------|------|
| resource_id | string | 资源 ID（必填） |
| metric_type | string | 指标类型，如 cpu_usage, mem_usage（必填） |
| hours | int | 时间范围（小时），默认 24，最大 720 |

**调用示例**：
```
用户：查看资源 res001 最近 24 小时的 CPU 趋势
Skill：调用 showtime_get_metric_trend（resource_id="res001", metric_type="cpu_usage", hours=24）

用户：查看资源 res001 最近 7 天的内存趋势
Skill：调用 showtime_get_metric_trend（resource_id="res001", metric_type="mem_usage", hours=168）
```

---

#### showtime_get_metric_summary

获取资源的指标汇总信息。

| 参数 | 类型 | 说明 |
|------|------|------|
| resource_id | string | 资源 ID（必填） |

**调用示例**：
```
用户：查看资源 res001 的指标汇总
Skill：调用 showtime_get_metric_summary（resource_id="res001"）
```

---

#### showtime_list_latest_metrics

获取资源的最新指标值。

| 参数 | 类型 | 说明 |
|------|------|------|
| resource_id | string | 资源 ID（必填） |

**调用示例**：
```
用户：查看资源 res001 的最新指标
Skill：调用 showtime_list_latest_metrics（resource_id="res001"）
```

---

### RAG 知识库工具

#### showtime_rag_chat

调用 RAG 问答，自动创建会话并保存历史。

| 参数 | 类型 | 说明 |
|------|------|------|
| kb_id | string | 知识库 ID（与 session_id 二选一） |
| question | string | 问题内容（必填） |
| session_id | string | 会话 ID（可选，用于继续会话） |

**调用示例**：
```
用户：在知识库 kb001 中问"CAS虚拟机状态有哪些？"
Skill：调用 showtime_rag_chat（kb_id="kb001", question="CAS虚拟机状态有哪些？"）

用户：继续 sess001 会话，问"那如何关机？"
Skill：调用 showtime_rag_chat（session_id="sess001", question="那如何关机？"）
```

---

#### showtime_list_sessions

列出知识库下的所有会话。

| 参数 | 类型 | 说明 |
|------|------|------|
| kb_id | string | 知识库 ID（必填） |

**调用示例**：
```
用户：请列出知识库 kb001 的所有会话
Skill：调用 showtime_list_sessions（kb_id="kb001"）
```

---

#### showtime_delete_session

删除指定会话。

| 参数 | 类型 | 说明 |
|------|------|------|
| session_id | string | 会话 ID（必填） |

**调用示例**：
```
用户：删除会话 sess001
Skill：调用 showtime_delete_session（session_id="sess001"）
```

---

#### showtime_list_knowledge_bases

列出所有知识库（无参数）。

**调用示例**：
```
用户：列出所有知识库
Skill：调用 showtime_list_knowledge_bases（）
```

---

## 返回格式

### 资源列表返回（Markdown 表格）

```
# ShowTime Resources (Total: N)

| Name | Platform | IP Address | Port | Status |
|------|----------|------------|------|--------|
| Server1 | CAS | 192.168.1.100 | 443 | Normal |
```

### 资源详情返回（JSON）

```json
{
  "id": "resource-id",
  "resourceName": "Resource Name",
  "platform": "workspace|uis|cas|onestor",
  "ipAddress": "192.168.1.100",
  "port": 443,
  "protocol": "HTTPS",
  "usable": 1
}
```

### 指标类型返回（Markdown）

```
# Available Metric Types

## CPU Metrics
- **cpu_usage**: CPU utilization

## Memory Metrics
- **mem_usage**: Memory utilization
```

### 指标趋势/汇总/最新指标返回（JSON）

```json
[
  {"metricType": "cpu_usage", "metricValue": "45.2", "reportTime": "2024-01-15T10:00:00"}
]
```

### RAG 问答返回（JSON）

```json
{
  "state": 0,
  "data": {
    "answer": "AI 回答内容",
    "session_id": "会话ID"
  }
}
```

## 错误处理

- 如果资源不存在：`"Error: Resource not found..."`
- 如果知识库为空：`"Knowledge base is empty, please build first"`
- 如果知识库不存在：`"Error: Knowledge base {kb_id} not found"`
- 如果会话不存在：`"Error: Session {session_id} not found"`
- 如果 API 请求失败：`"Error: API request failed with status..."`

## 使用场景示例

### 1. 查看资源列表

```
用户：列出所有 UIS 资源
Skill：调用 showtime_list_resources（platform="uis"）
```

### 2. 查看资源详情

```
用户：查看资源 res001 的详细信息
Skill：调用 showtime_get_resource_detail（resource_id="res001"）
```

### 3. 查看指标类型

```
用户：列出 CAS 平台支持的指标类型
Skill：调用 showtime_list_metric_types（platform="cas"）
```

### 4. 查看指标趋势

```
用户：查看资源 res001 最近 24 小时的 CPU 使用率趋势
Skill：调用 showtime_get_metric_trend（resource_id="res001", metric_type="cpu_usage", hours=24）
```

### 5. 查看最新指标

```
用户：查看资源 res001 的最新监控数据
Skill：调用 showtime_list_latest_metrics（resource_id="res001"）
```

### 6. RAG 知识库问答

```
用户：在知识库 kb001 中问"CAS虚拟化平台支持哪些功能？"
Skill：调用 showtime_rag_chat（kb_id="kb001", question="CAS虚拟化平台支持哪些功能？"）
```

### 7. 继续对话

```
用户：继续会话 sess001，问"如何创建虚拟机？"
Skill：调用 showtime_rag_chat（session_id="sess001", question="如何创建虚拟机？"）
```

### 8. 管理会话

```
用户：列出知识库 kb001 的所有会话
Skill：调用 showtime_list_sessions（kb_id="kb001"）

用户：删除会话 sess001
Skill：调用 showtime_delete_session（session_id="sess001"）
```

### 9. 管理知识库

```
用户：列出所有知识库
Skill：调用 showtime_list_knowledge_bases（）
```