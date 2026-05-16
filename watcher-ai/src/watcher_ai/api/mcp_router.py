"""MCP RAG HTTP 路由 - 暴露 MCP 工具为 HTTP 端点"""
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
from typing import Optional

from watcher_ai.api.mcp_rag import rag_chat as mcp_rag_chat, list_sessions as mcp_list_sessions, delete_session as mcp_delete_session


router = APIRouter(prefix="/mcp", tags=["mcp"])


class RagChatRequest(BaseModel):
    """MCP RAG 问答请求"""
    kb_id: Optional[str] = None
    question: str
    session_id: Optional[str] = None


class ListSessionsRequest(BaseModel):
    """MCP 列出会话请求"""
    kb_id: str


class DeleteSessionRequest(BaseModel):
    """MCP 删除会话请求"""
    session_id: str


@router.post("/rag_chat")
def mcp_rag_chat_endpoint(req: RagChatRequest):
    """MCP rag_chat 工具 HTTP 端点"""
    from watcher_ai.api.mcp_rag import McpRagRequest
    try:
        mcp_req = McpRagRequest(
            kb_id=req.kb_id,
            question=req.question,
            session_id=req.session_id
        )
        return mcp_rag_chat(mcp_req)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.post("/list_sessions")
def mcp_list_sessions_endpoint(req: ListSessionsRequest):
    """MCP list_sessions 工具 HTTP 端点"""
    try:
        return mcp_list_sessions(req.kb_id)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.post("/delete_session")
def mcp_delete_session_endpoint(req: DeleteSessionRequest):
    """MCP delete_session 工具 HTTP 端点"""
    try:
        return mcp_delete_session(req.session_id)
    except ValueError as e:
        raise HTTPException(status_code=404, detail=str(e))