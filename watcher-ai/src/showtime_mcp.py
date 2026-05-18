import os
import sys
import json
from typing import Optional, List, Dict, Any
from enum import Enum

import httpx
import requests
from pydantic import BaseModel, Field, field_validator, ConfigDict
from mcp.server.fastmcp import FastMCP

# Add watcher-ai src to path for imports
sys.path.insert(0, os.path.join(os.path.dirname(__file__), "watcher_ai"))

from watcher_ai.services.kb_service import KBService
from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.chat_repository import ChatRepository
from watcher_ai.services.llm_service import LLMService

# Initialize the MCP server
mcp = FastMCP("showtime_mcp")

# Configuration
API_BASE_URL = os.getenv("SHOWTIME_API_URL", "http://localhost:8080")
API_TOKEN = os.getenv("SHOWTIME_API_TOKEN", "")

# Enums
class ResponseFormat(str, Enum):
    """Output format for tool responses."""
    MARKDOWN = "markdown"
    JSON = "json"


# Pydantic Models
class ResourceListInput(BaseModel):
    """Input model for listing resources."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    platform: Optional[str] = Field(
        default=None,
        description="Filter by platform: workspace, uis, cas"
    )
    resource_name: Optional[str] = Field(
        default=None,
        description="Filter by resource name (partial match)"
    )
    ip_address: Optional[str] = Field(
        default=None,
        description="Filter by IP address"
    )
    limit: int = Field(default=100, ge=1, le=500, description="Maximum results to return")
    offset: int = Field(default=0, ge=0, description="Number of results to skip")


class ResourceDetailInput(BaseModel):
    """Input model for getting resource details."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    resource_id: str = Field(..., description="Resource ID to query", min_length=1)


class MetricListInput(BaseModel):
    """Input model for listing metrics."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    resource_id: Optional[str] = Field(default=None, description="Filter by resource ID")
    platform: Optional[str] = Field(default=None, description="Filter by platform")
    metric_type: Optional[str] = Field(default=None, description="Filter by metric type")
    limit: int = Field(default=100, ge=1, le=500)
    offset: int = Field(default=0, ge=0)


class MetricTrendInput(BaseModel):
    """Input model for getting metric trend data."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    resource_id: str = Field(..., description="Resource ID to query", min_length=1)
    metric_type: str = Field(..., description="Metric type (e.g., cpu_usage, mem_usage)", min_length=1)
    hours: int = Field(default=24, ge=1, le=720, description="Time range in hours")


class MetricTypesInput(BaseModel):
    """Input model for listing available metric types."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    platform: Optional[str] = Field(default=None, description="Filter by platform")


class MetricSummaryInput(BaseModel):
    """Input model for getting resource metric summary."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    resource_id: str = Field(..., description="Resource ID to query", min_length=1)


class RagChatInput(BaseModel):
    """Input model for RAG chat."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    kb_id: Optional[str] = Field(default=None, description="Knowledge base ID (required if session_id not provided)")
    question: str = Field(..., description="Question to ask", min_length=1)
    session_id: Optional[str] = Field(default=None, description="Session ID for continuing a conversation")


class ListSessionsInput(BaseModel):
    """Input model for listing sessions."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    kb_id: str = Field(..., description="Knowledge base ID", min_length=1)


class DeleteSessionInput(BaseModel):
    """Input model for deleting a session."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )

    session_id: str = Field(..., description="Session ID to delete", min_length=1)


class ListKnowledgeBasesInput(BaseModel):
    """Input model for listing knowledge bases."""
    model_config = ConfigDict(
        str_strip_whitespace=True,
        validate_assignment=True,
        extra='forbid'
    )


# Shared utility functions
def _get_headers() -> Dict[str, str]:
    """Get HTTP headers with authentication."""
    headers = {"Content-Type": "application/json"}
    if API_TOKEN:
        headers["token"] = API_TOKEN
    return headers


def _ok_response(data: Any) -> Dict[str, Any]:
    """Create a success response."""
    return {"state": 0, "data": data}


async def _make_api_request(endpoint: str, method: str = "GET", **kwargs) -> dict:
    """Reusable function for all API calls."""
    async with httpx.AsyncClient() as client:
        response = await client.request(
            method,
            f"{API_BASE_URL}{endpoint}",
            headers=_get_headers(),
            timeout=30.0,
            **kwargs
        )
        response.raise_for_status()
        return response.json()


def _handle_api_error(e: Exception) -> str:
    """Consistent error formatting across all tools."""
    if isinstance(e, httpx.HTTPStatusError):
        if e.response.status_code == 404:
            return f"Error: Resource not found at {e.request.url}. Please check the ID is correct."
        elif e.response.status_code == 403:
            return "Error: Permission denied. You don't have access to this resource."
        elif e.response.status_code == 401:
            return "Error: Authentication failed. Please check your API token."
        elif e.response.status_code == 429:
            return "Error: Rate limit exceeded. Please wait before making more requests."
        return f"Error: API request failed with status {e.response.status_code}"
    elif isinstance(e, httpx.TimeoutException):
        return "Error: Request timed out. The ShowTime server may be unavailable. Please try again."
    elif isinstance(e, httpx.ConnectError):
        return f"Error: Cannot connect to ShowTime API at {API_BASE_URL}. Please check if the server is running."
    return f"Error: Unexpected error occurred: {type(e).__name__}: {str(e)}"


def _format_resource_markdown(resources: List[Dict], total: int) -> str:
    """Format resources as markdown table."""
    if not resources:
        return "No resources found matching the criteria."

    lines = [
        f"# ShowTime Resources (Total: {total})",
        "",
        "| Name | Platform | IP Address | Port | Status |",
        "|------|----------|------------|------|--------|"
    ]

    for r in resources:
        status = "Normal" if r.get("usable") == 1 else "Abnormal"
        name = r.get("resourceName") or r.get("id", "N/A")
        platform = (r.get("platform") or "N/A").upper()
        ip = r.get("ipAddress") or "N/A"
        port = r.get("port") or "N/A"
        lines.append(f"| {name} | {platform} | {ip} | {port} | {status} |")

    return "\n".join(lines)


# Tool definitions
@mcp.tool(
    name="showtime_list_resources",
    annotations={
        "title": "List ShowTime Resources",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_list_resources(params: ResourceListInput) -> str:
    """
    List all resources in the ShowTime monitoring system with optional filters.

    This tool retrieves resources that have been registered in the ShowTime
    monitoring platform. Resources can be filtered by platform type, name,
    or IP address.

    Args:
        params (ResourceListInput): Validated input parameters containing:
            - platform (Optional[str]): Filter by platform type
            - resource_name (Optional[str]): Filter by resource name (partial match)
            - ip_address (Optional[str]): Filter by IP address
            - limit (int): Maximum results to return (default: 100)
            - offset (int): Number of results to skip (default: 0)

    Returns:
        str: Formatted resource list with the following schema:

        Success response (Markdown table):
        # ShowTime Resources (Total: N)
        | Name | Platform | IP Address | Port | Status |
        ...

        Error response:
        "Error: <error message>"

    Examples:
        - Use when: "List all CAS resources" -> platform="cas"
        - Use when: "Find resources with IP 192.168.1.100" -> ip_address="192.168.1.100"
        - Don't use when: You have a specific resource ID (use showtime_get_resource_detail instead)
    """
    try:
        params_dict = {"page": params.offset // params.limit, "size": params.limit}
        if params.platform:
            params_dict["platform"] = params.platform
        if params.resource_name:
            params_dict["resourceName"] = params.resource_name
        if params.ip_address:
            params_dict["ipAddress"] = params.ip_address

        data = await _make_api_request(
            "/resource/list",
            params=params_dict
        )

        resources = data.get("data", [])
        total = data.get("totalLength", len(resources))

        return _format_resource_markdown(resources, total)

    except Exception as e:
        return _handle_api_error(e)


@mcp.tool(
    name="showtime_get_resource_detail",
    annotations={
        "title": "Get ShowTime Resource Detail",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_get_resource_detail(params: ResourceDetailInput) -> str:
    """
    Get detailed information about a specific resource in ShowTime.

    This tool retrieves comprehensive details for a single resource,
    including its configuration, status, and management credentials.

    Args:
        params (ResourceDetailInput): Validated input parameters containing:
            - resource_id (str): The unique identifier of the resource

    Returns:
        str: JSON-formatted string containing resource details:

        Success response:
        {
            "id": "resource-id",
            "resourceName": "Resource Name",
            "platform": "workspace|uis|cas",
            "ipAddress": "192.168.1.100",
            "port": 443,
            "protocol": "HTTPS",
            "usable": 1,
            ...
        }

        Error response:
        "Error: <error message>"
    """
    try:
        data = await _make_api_request(f"/resource/detail/{params.resource_id}")

        if data.get("state") != 0:
            return f"Error: {data.get('failureMessage', 'Failed to get resource detail')}"

        return json.dumps(data.get("data", {}), indent=2, ensure_ascii=False)

    except Exception as e:
        return _handle_api_error(e)


@mcp.tool(
    name="showtime_list_metric_types",
    annotations={
        "title": "List Available Metric Types",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_list_metric_types(params: MetricTypesInput) -> str:
    """
    List all available metric types in the ShowTime monitoring system.

    This tool returns the complete list of metrics that can be queried,
    such as CPU usage, memory usage, disk usage, network throughput, etc.

    Args:
        params (MetricTypesInput): Validated input parameters containing:
            - platform (Optional[str]): Filter metrics by platform type

    Returns:
        str: Formatted metric types list:

        Success response (Markdown):
        # Available Metric Types

        ## CPU Metrics
        - cpu_usage: CPU utilization

        ## Memory Metrics
        - mem_usage: Memory utilization

        ...

        Error response:
        "Error: <error message>"
    """
    try:
        endpoint = "/metric/types"
        if params.platform:
            endpoint += f"?platform={params.platform}"

        data = await _make_api_request(endpoint)

        if data.get("state") != 0:
            return f"Error: {data.get('failureMessage', 'Failed to get metric types')}"

        metrics = data.get("data", [])

        if not metrics:
            return "No metric types found."

        lines = ["# Available Metric Types", ""]

        # Group metrics by category
        categories = {
            "cpu": [],
            "mem": [],
            "disk": [],
            "net": [],
            "other": []
        }

        for m in metrics:
            code = m.get("code", "")
            desc = m.get("desc", code)
            is_static = m.get("static", "false") == "true"

            metric_info = f"- **{code}**: {desc}"
            if is_static:
                metric_info += " (static)"

            if "cpu" in code.lower():
                categories["cpu"].append(metric_info)
            elif "mem" in code.lower():
                categories["mem"].append(metric_info)
            elif "disk" in code.lower():
                categories["disk"].append(metric_info)
            elif "net" in code.lower():
                categories["net"].append(metric_info)
            else:
                categories["other"].append(metric_info)

        for category, items in categories.items():
            if items:
                category_names = {
                    "cpu": "CPU Metrics",
                    "mem": "Memory Metrics",
                    "disk": "Disk Metrics",
                    "net": "Network Metrics",
                    "other": "Other Metrics"
                }
                lines.append(f"## {category_names[category]}")
                lines.extend(items)
                lines.append("")

        return "\n".join(lines)

    except Exception as e:
        return _handle_api_error(e)


@mcp.tool(
    name="showtime_get_metric_trend",
    annotations={
        "title": "Get ShowTime Metric Trend",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_get_metric_trend(params: MetricTrendInput) -> str:
    """
    Get historical trend data for a specific metric of a resource.

    This tool retrieves time-series data for monitoring charts,
    allowing analysis of metric changes over a time period.

    Args:
        params (MetricTrendInput): Validated input parameters containing:
            - resource_id (str): The resource identifier
            - metric_type (str): The metric type to query
            - hours (int): Time range in hours (1-720, default: 24)

    Returns:
        str: JSON-formatted metric trend data:

        Success response:
        [
            {"metricType": "cpu_usage", "metricValue": "45.2", "reportTime": "2024-01-15T10:00:00"},
            {"metricType": "cpu_usage", "metricValue": "46.1", "reportTime": "2024-01-15T10:05:00"},
            ...
        ]

        Error response:
        "Error: <error message>"
    """
    try:
        data = await _make_api_request(
            f"/metric/trend/{params.resource_id}/{params.metric_type}",
            params={"hours": params.hours}
        )

        if data.get("state") != 0:
            return f"Error: {data.get('failureMessage', 'Failed to get metric trend')}"

        trend_data = data.get("data", [])

        if not trend_data:
            return f"No trend data found for metric '{params.metric_type}' in the past {params.hours} hours."

        return json.dumps(trend_data, indent=2, ensure_ascii=False)

    except Exception as e:
        return _handle_api_error(e)


@mcp.tool(
    name="showtime_get_metric_summary",
    annotations={
        "title": "Get ShowTime Resource Metric Summary",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_get_metric_summary(params: MetricSummaryInput) -> str:
    """
    Get metric summary statistics for a specific resource.

    This tool provides an overview of monitoring data for a resource,
    including total metrics collected and last report time.

    Args:
        params (MetricSummaryInput): Validated input parameters containing:
            - resource_id (str): The resource identifier

    Returns:
        str: JSON-formatted metric summary:

        Success response:
        {
            "resourceId": "resource-id",
            "ipAddress": "192.168.1.100",
            "platform": "cas",
            "totalMetrics": 1234,
            "lastReportTime": "2024-01-15T10:30:00"
        }

        Error response:
        "Error: <error message>"
    """
    try:
        data = await _make_api_request(f"/metric/summary/{params.resource_id}")

        if data.get("state") != 0:
            return f"Error: {data.get('failureMessage', 'Failed to get metric summary')}"

        return json.dumps(data.get("data", {}), indent=2, ensure_ascii=False)

    except Exception as e:
        return _handle_api_error(e)


@mcp.tool(
    name="showtime_list_latest_metrics",
    annotations={
        "title": "Get Latest Metrics for Resource",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_list_latest_metrics(params: ResourceDetailInput) -> str:
    """
    Get the latest metric values for a specific resource.

    This tool retrieves the most recent value for each metric type
    associated with a resource.

    Args:
        params (ResourceDetailInput): Validated input parameters containing:
            - resource_id (str): The resource identifier

    Returns:
        str: JSON-formatted latest metrics:

        Success response:
        {
            "data": [
                {"metricType": "cpu_usage", "metricValue": "45.2", "reportTime": "2024-01-15T10:30:00"},
                {"metricType": "mem_usage", "metricValue": "62.8", "reportTime": "2024-01-15T10:30:00"},
                ...
            ]
        }

        Error response:
        "Error: <error message>"
    """
    try:
        data = await _make_api_request(f"/metric/latest/{params.resource_id}")

        if data.get("state") != 0:
            return f"Error: {data.get('failureMessage', 'Failed to get latest metrics')}"

        return json.dumps(data.get("data", []), indent=2, ensure_ascii=False)

    except Exception as e:
        return _handle_api_error(e)


@mcp.tool(
    name="showtime_rag_chat",
    annotations={
        "title": "RAG Chat with Knowledge Base",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_rag_chat(params: RagChatInput) -> str:
    """
    RAG chat with knowledge base, automatically creating session and saving history.

    This tool allows you to ask questions against a knowledge base and get AI-powered
    answers based on the retrieved context.

    Args:
        params (RagChatInput): Validated input parameters containing:
            - kb_id (Optional[str]): Knowledge base ID (required if session_id not provided)
            - question (str): Question to ask
            - session_id (Optional[str]): Session ID for continuing a conversation

    Returns:
        str: JSON-formatted response:

        Success response:
        {
            "state": 0,
            "data": {
                "answer": "AI answer content",
                "session_id": "session-id"
            }
        }

        Error response:
        "Error: <error message>"
    """
    if not params.kb_id and not params.session_id:
        return "Error: kb_id or session_id is required"

    try:
        # 1. Get or create session
        if params.session_id:
            session = ChatRepository.get_session_by_id(params.session_id)
            if not session:
                return f"Error: Session {params.session_id} not found"
            kb_id = session['kb_id']
        else:
            kb_id = params.kb_id
            if not kb_id:
                return "Error: kb_id is required when session_id is not provided"
            # Check if KB exists
            kb = KBService.get_by_id(kb_id)
            if not kb:
                return f"Error: Knowledge base {kb_id} not found"
            # Create new session
            session = ChatRepository.create_session(
                user_id="mcp_system",
                kb_id=kb_id,
                title=params.question[:50]
            )

        # 2. Execute RAG query
        assert kb_id is not None, "kb_id should not be None here"
        chunk_count = ChromaService.get_count(kb_id)
        if chunk_count == 0:
            return json.dumps(_ok_response({
                "answer": "Knowledge base is empty, please build first",
                "session_id": session['id']
            }), ensure_ascii=False)

        results = ChromaService.search(kb_id, params.question, top_k=3)

        if not results.get("documents") or not results["documents"][0]:
            answer = "没有找到相关文档"
        else:
            context_parts = []
            for doc in results["documents"][0]:
                context_parts.append(doc)
            context = "\n\n".join(context_parts)
            answer = LLMService.chat(params.question, context)

        # 3. Save messages
        ChatRepository.save_message(session['id'], "user", params.question)
        ChatRepository.save_message(session['id'], "assistant", answer)

        # 4. Update message count
        messages = ChatRepository.list_messages_by_session(session['id'])
        ChatRepository.update_message_count(session['id'], len(messages))

        return json.dumps(_ok_response({
            "answer": answer,
            "session_id": session['id']
        }), ensure_ascii=False)

    except Exception as e:
        return f"Error: {str(e)}"


@mcp.tool(
    name="showtime_list_sessions",
    annotations={
        "title": "List Sessions in Knowledge Base",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_list_sessions(params: ListSessionsInput) -> str:
    """
    List all sessions in a knowledge base.

    Args:
        params (ListSessionsInput): Validated input parameters containing:
            - kb_id (str): Knowledge base ID

    Returns:
        str: JSON-formatted session list:

        Success response:
        {
            "state": 0,
            "data": {
                "sessions": [...],
                "total": 10,
                "page": 1,
                "page_size": 100
            }
        }

        Error response:
        "Error: <error message>"
    """
    try:
        result = ChatRepository.list_sessions_by_kb(params.kb_id, page=1, page_size=100)
        return json.dumps(_ok_response(result), ensure_ascii=False)
    except Exception as e:
        return f"Error: {str(e)}"


@mcp.tool(
    name="showtime_delete_session",
    annotations={
        "title": "Delete a Session",
        "readOnlyHint": False,
        "destructiveHint": True,
        "idempotentHint": False,
        "openWorldHint": False
    }
)
async def showtime_delete_session(params: DeleteSessionInput) -> str:
    """
    Delete a specific chat session.

    Args:
        params (DeleteSessionInput): Validated input parameters containing:
            - session_id (str): Session ID to delete

    Returns:
        str: JSON-formatted response:

        Success response:
        {
            "state": 0,
            "data": {
                "message": "deleted"
            }
        }

        Error response:
        "Error: <error message>"
    """
    try:
        session = ChatRepository.get_session_by_id(params.session_id)
        if not session:
            return f"Error: Session {params.session_id} not found"
        ChatRepository.delete_session(params.session_id)
        return json.dumps(_ok_response({"message": "deleted"}), ensure_ascii=False)
    except Exception as e:
        return f"Error: {str(e)}"


@mcp.tool(
    name="showtime_list_knowledge_bases",
    annotations={
        "title": "List All Knowledge Bases",
        "readOnlyHint": True,
        "destructiveHint": False,
        "idempotentHint": True,
        "openWorldHint": False
    }
)
async def showtime_list_knowledge_bases(params: ListKnowledgeBasesInput) -> str:
    """
    List all available knowledge bases.

    Returns:
        str: JSON-formatted knowledge base list:

        Success response:
        {
            "state": 0,
            "data": [
                {
                    "id": "kb001",
                    "name": "CAS Documentation",
                    "description": "...",
                    "status": "ready",
                    "chunk_count": 100
                }
            ]
        }

        Error response:
        "Error: <error message>"
    """
    try:
        import watcher_ai.services.kb_service as kb_module
        kb_module.init()
        kbs = KBService.list_all()
        # 转换 datetime 为字符串，避免 JSON 序列化失败
        for kb in kbs:
            for key, value in kb.items():
                if hasattr(value, 'isoformat'):
                    kb[key] = value.isoformat()
        return json.dumps(_ok_response(kbs), ensure_ascii=False)
    except Exception as e:
        return f"Error: {str(e)}"


if __name__ == "__main__":
    import argparse

    parser = argparse.ArgumentParser(description="ShowTime MCP Server")
    parser.add_argument("--http", action="store_true", help="启用 HTTP 模式")
    args = parser.parse_args()

    if args.http:
        print("启动 HTTP 服务: http://0.0.0.0:7777")
        mcp.run(transport="streamable-http")
    else:
        print("启动 stdio 模式 MCP Server")
        mcp.run(transport="stdio")
