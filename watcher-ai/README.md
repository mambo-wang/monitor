# ShowTime MCP Server

提供 ShowTime 监控系统的 MCP (Model Context Protocol) 接口，使 AI Agent 能够自然语言查询监控数据。

## 功能

- 资源列表查询
- 资源详情查询
- 指标类型列表
- 指标趋势数据查询
- 指标汇总统计
- 最新指标值查询

## 安装

```bash
cd watcher-ai
pip install -r requirements.txt
```

## 配置

创建 `.env` 文件或设置环境变量：

```bash
export SHOWTIME_API_URL=http://localhost:8080
export SHOWTIME_API_TOKEN=your-token-here
```

## 运行

### 本地模式 (stdio)
```bash
python src/showtime_mcp.py
```

### HTTP 模式
```bash
python src/showtime_mcp.py --transport streamable_http --port 8000
```

## MCP 工具

| 工具名称 | 描述 |
|---------|------|
| `showtime_list_resources` | 列出所有资源 |
| `showtime_get_resource_detail` | 获取资源详情 |
| `showtime_list_metric_types` | 列出指标类型 |
| `showtime_get_metric_trend` | 获取指标趋势 |
| `showtime_get_metric_summary` | 获取资源指标汇总 |
| `showtime_list_latest_metrics` | 获取最新指标值 |

## 使用示例

### CodeBuddy 配置

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
