from typing import Dict, List, Optional
from datetime import datetime
import uuid
import os
import json
import chromadb

# 数据持久化目录
DATA_DIR = os.path.join(os.path.dirname(__file__), "../../../data")
os.makedirs(DATA_DIR, exist_ok=True)
KB_STORE_FILE = os.path.join(DATA_DIR, "kb_store.json")

# 内存存储
_kb_store: Dict[str, dict] = {}
_initialized = False

def _load_from_disk():
    """从磁盘加载 KB 数据"""
    global _kb_store
    if os.path.exists(KB_STORE_FILE):
        try:
            with open(KB_STORE_FILE, "r") as f:
                data = json.load(f)
                for kb in data:
                    # 转换 ISO 字符串回 datetime
                    if isinstance(kb.get("created_at"), str):
                        kb["created_at"] = datetime.fromisoformat(kb["created_at"])
                    if isinstance(kb.get("updated_at"), str):
                        kb["updated_at"] = datetime.fromisoformat(kb["updated_at"])
                    _kb_store[kb["id"]] = kb
        except Exception as e:
            print(f"Error loading KB store: {e}")

def _save_to_disk():
    """保存 KB 数据到磁盘"""
    try:
        data = list(_kb_store.values())
        # 转换 datetime 为 ISO 字符串
        for kb in data:
            if isinstance(kb.get("created_at"), datetime):
                kb["created_at"] = kb["created_at"].isoformat()
            if isinstance(kb.get("updated_at"), datetime):
                kb["updated_at"] = kb["updated_at"].isoformat()
        with open(KB_STORE_FILE, "w") as f:
            json.dump(data, f, indent=2)
    except Exception as e:
        print(f"Error saving KB store: {e}")

def _sync_from_chroma():
    """从 ChromaDB 同步 KB 列表（用于服务重启后恢复）"""
    global _kb_store
    chroma_path = os.path.join(os.path.dirname(__file__), "../../../src/chroma_db")
    if not os.path.exists(chroma_path):
        return
    try:
        client = chromadb.PersistentClient(path=chroma_path)
        collections = client.list_collections()
        chroma_kb_ids = set()
        for col in collections:
            if col.name.startswith("kb_"):
                kb_id = col.name[3:]  # 去掉 "kb_" 前缀
                chroma_kb_ids.add(kb_id)
                # 如果 KB 不在内存中，从 ChromaDB 恢复
                if kb_id not in _kb_store:
                    try:
                        collection = client.get_collection(col.name)
                        chunk_count = collection.count()
                        # 创建占位 KB（从 ChromaDB 恢复）
                        _kb_store[kb_id] = {
                            "id": kb_id,
                            "name": f"KB_{kb_id[:8]}",
                            "description": "Recovered from ChromaDB",
                            "status": "ready" if chunk_count > 0 else "idle",
                            "document_count": 0,
                            "chunk_count": chunk_count,
                            "created_at": datetime.now(),
                            "updated_at": datetime.now()
                        }
                    except Exception as e:
                        print(f"Error restoring KB {kb_id}: {e}")
        # 清理内存中存在但 ChromaDB 中不存在的 KB
        removed = []
        for kb_id in list(_kb_store.keys()):
            if kb_id not in chroma_kb_ids:
                removed.append(kb_id)
        for kb_id in removed:
            del _kb_store[kb_id]
        if removed:
            _save_to_disk()
    except Exception as e:
        print(f"Error syncing from ChromaDB: {e}")

def init():
    """初始化 KBService"""
    global _initialized
    if not _initialized:
        _load_from_disk()
        _sync_from_chroma()
        _initialized = True

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
        _save_to_disk()
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
            _save_to_disk()
            return True
        return False

    @staticmethod
    def update_status(kb_id: str, status: str):
        if kb_id in _kb_store:
            _kb_store[kb_id]["status"] = status
            _kb_store[kb_id]["updated_at"] = datetime.now()
            _save_to_disk()

    @staticmethod
    def update_counts(kb_id: str, doc_count: int, chunk_count: int):
        if kb_id in _kb_store:
            _kb_store[kb_id]["document_count"] = doc_count
            _kb_store[kb_id]["chunk_count"] = chunk_count
            _kb_store[kb_id]["updated_at"] = datetime.now()
            _save_to_disk()
