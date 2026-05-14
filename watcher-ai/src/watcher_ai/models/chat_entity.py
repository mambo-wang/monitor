"""对话历史实体类"""
from dataclasses import dataclass
from datetime import datetime
from typing import Optional, List, Dict, Any
import json


@dataclass
class ChatSession:
    """对话会话实体"""
    id: str
    user_id: str
    kb_id: str
    title: str = ""
    message_count: int = 0
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None

    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'user_id': self.user_id,
            'kb_id': self.kb_id,
            'title': self.title,
            'message_count': self.message_count,
            'created_at': self.created_at,
            'updated_at': self.updated_at
        }

    @classmethod
    def from_dict(cls, data: dict) -> 'ChatSession':
        return cls(
            id=data['id'],
            user_id=data['user_id'],
            kb_id=data['kb_id'],
            title=data.get('title', ''),
            message_count=data.get('message_count', 0),
            created_at=data.get('created_at'),
            updated_at=data.get('updated_at')
        )


@dataclass
class ChatMessage:
    """对话消息实体"""
    id: Optional[int] = None
    session_id: str = ""
    role: str = ""
    content: str = ""
    sources: Optional[List[Dict]] = None
    created_at: Optional[datetime] = None

    def to_dict(self) -> dict:
        return {
            'id': self.id,
            'session_id': self.session_id,
            'role': self.role,
            'content': self.content,
            'sources': self.sources,
            'created_at': self.created_at
        }

    @classmethod
    def from_dict(cls, data: dict) -> 'ChatMessage':
        sources = data.get('sources')
        if sources and isinstance(sources, str):
            sources = json.loads(sources)
        return cls(
            id=data.get('id'),
            session_id=data['session_id'],
            role=data['role'],
            content=data['content'],
            sources=sources,
            created_at=data.get('created_at')
        )
