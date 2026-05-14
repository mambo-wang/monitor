from typing import Dict, List, Optional
from datetime import datetime
import uuid

# 内存存储
_kb_store: Dict[str, dict] = {}

class KBService:
    @staticmethod
    def create(name: str, description: str = "") -> dict:
        kb_id = str(uuid.uuid4())
        now = datetime.now()
        kb = {
            "id": kb_id,
            "name": name,
            "description": description,
            "status": "idle",
            "document_count": 0,
            "chunk_count": 0,
            "created_at": now,
            "updated_at": now
        }
        _kb_store[kb_id] = kb
        return kb

    @staticmethod
    def list_all() -> List[dict]:
        return list(_kb_store.values())

    @staticmethod
    def get_by_id(kb_id: str) -> Optional[dict]:
        return _kb_store.get(kb_id)

    @staticmethod
    def delete(kb_id: str) -> bool:
        if kb_id in _kb_store:
            del _kb_store[kb_id]
            return True
        return False

    @staticmethod
    def update_status(kb_id: str, status: str):
        if kb_id in _kb_store:
            _kb_store[kb_id]["status"] = status
            _kb_store[kb_id]["updated_at"] = datetime.now()

    @staticmethod
    def update_counts(kb_id: str, doc_count: int, chunk_count: int):
        if kb_id in _kb_store:
            _kb_store[kb_id]["document_count"] = doc_count
            _kb_store[kb_id]["chunk_count"] = chunk_count
            _kb_store[kb_id]["updated_at"] = datetime.now()
