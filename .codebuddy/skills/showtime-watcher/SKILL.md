---
name: ShowTime 监控系统
description: |
  ShowTime 全方位监控系统 MCP 服务集成。当用户需要查询监控数据、资源信息、指标趋势时使用此 skill。
  支持查询资源列表、资源详情、指标类型、指标趋势、指标汇总等监控相关操作。
---

## 用途

ShowTime 是云基产品线的全方位监控系统，核心功能是指标采集和监控报表展示。此 skill 使 AI Agent 能够：
- 查询已注册的资源列表
- 获取特定资源的详细信息
- 查询可用的监控指标类型
- 获取资源指标的历史趋势数据
- 获取监控数据汇总统计

## 触发条件

当用户提到以下意图时使用此 skill：
- "查看监控资源"
- "查询资源列表"
- "获取资源详情"
- "查看 CPU/内存/磁盘使用率"
- "查询监控指标"
- "查看指标趋势"
- "ShowTime 监控"

## 工作流程

### 第一步：安装 MCP Server

确保 ShowTime MCP Server 已正确安装和配置：

```bash
# 安装依赖
cd watcher-ai
pip install -r requirements.txt

# 配置环境变量
export SHOWTIME_API_URL=http://localhost:8080
export SHOWTIME_API_TOKEN=your-token

# 运行 MCP Server
python src/showtime_mcp.py
```

### 第二步：使用 MCP 工具

#### 1. 列出所有资源

```python
# 列出所有资源
result = await showtime_list_resources(params)

# 按平台过滤
result = await showtime_list_resources(params)  # platform="cas"
```

#### 2. 获取资源详情

```python
# 获取特定资源的详细信息
result = await showtime_get_resource_detail(params)  # resource_id="xxx"
```

#### 3. 查询指标类型

```python
# 列出所有可用的指标类型
result = await showtime_list_metric_types(params)
```

#### 4. 获取指标趋势

```python
# 获取 CPU 使用率在过去 24 小时的趋势
result = await showtime_get_metric_trend(params)  # resource_id="xxx", metric_type="cpu_usage", hours=24
```

#### 5. 获取指标汇总

```python
# 获取资源的监控汇总
result = await showtime_get_metric_summary(params)  # resource_id="xxx"
```

## 支持的平台

- **Workspace**: 虚拟化桌面平台
- **UIS**: 统一智能存储平台
- **CAS**: 云管理平台
- **ONEStor**: 分布式存储平台

## 支持的指标类型

### CPU 指标
- `cpu_usage`: CPU 利用率
- `cpu_allocate_rate`: CPU 分配比

### 内存指标
- `mem_usage`: 内存利用率
- `mem_allocate_rate`: 内存分配比

### 磁盘指标
- `disk_usage`: 磁盘利用率
- `disk_iops`: 磁盘 IOPS
- `disk_latency`: 磁盘延迟
- `disk_throughput`: 磁盘吞吐量

### 网络指标
- `net_throughput`: 网络吞吐量

### 健康指标
- `health_info`: 健康度信息

## MCP Server 配置

在 CodeBuddy 中配置 MCP Server：

```json
{
  "mcpServers": {
    "showtime": {
      "command": "python",
      "args": ["/path/to/showtime_mcp.py"],
      "env": {
        "SHOWTIME_API_URL": "http://localhost:8080"
      }
    }
  }
}
```

## 注意事项

1. MCP Server 需要与 ShowTime 后端服务 (`watcher-agent`) 配合使用
2. 确保后端服务已启动并可访问
3. API Token 认证可选，取决于后端配置
4. 指标数据需要先通过采集任务获取
