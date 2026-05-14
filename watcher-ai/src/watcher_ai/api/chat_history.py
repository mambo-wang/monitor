"""对话历史 API 路由"""
from fastapi import APIRouter, Query, HTTPException
from typing import Optional, Any

from watcher_ai.services.chat_service import ChatService

router = APIRouter(prefix="/api/knowledge/chat", tags=["chat-history"])


def ok_response(data: Any):
    """包装成功响应"""
    return {"state": 0, "data": data}


@router.get("/sessions")
def list_sessions(
    user_id: str = Query(..., description="用户ID"),
    page: int = Query(1, ge=1, description="页码"),
    page_size: int = Query(20, ge=1, le=100, description="每页数量")
):
    """获取用户会话列表"""
    result = ChatService.get_user_sessions(user_id, page, page_size)
    return ok_response(result)


@router.get("/sessions/{session_id}")
def get_session_detail(session_id: str):
    """获取会话详情"""
    try:
        result = ChatService.get_session_detail(session_id)
        return ok_response(result)
    except ValueError as e:
        raise HTTPException(status_code=404, detail="会话不存在")


@router.delete("/sessions/{session_id}")
def delete_session(session_id: str):
    """删除会话（幂等）"""
    ChatService.delete_session(session_id)
    return ok_response({"message": "deleted"})
