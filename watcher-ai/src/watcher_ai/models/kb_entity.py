"""知识库实体类"""
from dataclasses import dataclass
from datetime import datetime
from typing import Optional

@dataclass
class KnowledgeBase:
    """知识库实体"""
    id: str
    name: str
    description: str = ""
    status: str = "idle"
    document_count: int = 0
    chunk_count: int = 0
    created_at: Optional[datetime] = None
    updated_at: Optional[datetime] = None
    
    def to_dict(self) -> dict:
        """转换为字典"""
        return {
            'id': self.id,
            'name': self.name,
            'description': self.description,
            'status': self.status,
            'document_count': self.document_count,
            'chunk_count': self.chunk_count,
            'created_at': self.created_at,
            'updated_at': self.updated_at
        }
    
    @classmethod
    def from_dict(cls, data: dict) -> 'KnowledgeBase':
        """从字典创建"""
        return cls(
            id=data['id'],
            name=data['name'],
            description=data.get('description', ''),
            status=data.get('status', 'idle'),
            document_count=data.get('document_count', 0),
            chunk_count=data.get('chunk_count', 0),
            created_at=data.get('created_at'),
            updated_at=data.get('updated_at')
        )
