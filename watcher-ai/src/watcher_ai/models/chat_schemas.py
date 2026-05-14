"""对话历史 Pydantic 模型"""
from pydantic import BaseModel
from typing import Optional, List, Dict, Any
from datetime import datetime


class ChatSessionResponse(BaseModel):
    """会话响应"""
    id: str
    user_id: str
    kb_id: str
    title: str = ""
    message_count: int = 0
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None


class ChatMessageResponse(BaseModel):
    """消息响应"""
    id: int
    session_id: str
    role: str
    content: str
    sources: Optional[List[Dict]] = None
    created_at: Optional[datetime] = None


class SessionListResponse(BaseModel):
    """会话列表响应"""
    sessions: List[Dict[str, Any]]
    total: int
    page: int
    page_size: int


class SessionDetailResponse(BaseModel):
    """会话详情响应"""
    session: Dict[str, Any]
    messages: List[Dict[str, Any]]
