from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class KnowledgeBaseCreate(BaseModel):
    name: str
    description: Optional[str] = ""

class KnowledgeBaseResponse(BaseModel):
    id: str
    name: str
    description: str
    status: str
    document_count: int = 0
    chunk_count: int = 0
    created_at: datetime
    updated_at: datetime
