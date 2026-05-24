"""知识库服务层 - 使用MySQL存储"""
from typing import Dict, List, Optional
from datetime import datetime
import os
import chromadb

from watcher_ai.services.kb_repository import KBRepository

# ChromaDB 配置
CHROMA_DB_PATH = os.path.join(os.path.dirname(__file__), "../../../src/chroma_db")

_initialized = False

def _sync_from_chroma():
    """从 ChromaDB 同步 KB 列表（用于服务重启后恢复）
    
    关键逻辑：如果 KB 已在 MySQL 中存在，跳过（保留 MySQL 数据）
    """
    if not os.path.exists(CHROMA_DB_PATH):
        return
    
    try:
        client = chromadb.PersistentClient(path=CHROMA_DB_PATH)
        collections = client.list_collections()
        
        for col in collections:
            if col.name.startswith("kb_"):
                kb_id = col.name[3:]  # 去掉 "kb_" 前缀
                
                # 关键改进：如果 KB 已在 MySQL 中存在，跳过（保留 MySQL 数据）
                if KBRepository.exists(kb_id):
                    print(f"KB {kb_id} already exists in MySQL, skipping sync")
                    continue
                
                # 从 ChromaDB 恢复不存在的 KB
                try:
                    collection = client.get_collection(col.name)
                    chunk_count = collection.count()
                    
                    kb_data = {
                        "id": kb_id,
                        "name": f"KB_{kb_id[:8]}",  # 使用默认名称
                        "description": "Recovered from ChromaDB",
                        "status": "ready" if chunk_count > 0 else "idle",
                        "document_count": 0,
                        "chunk_count": chunk_count,
                        "created_at": datetime.now(),
                        "updated_at": datetime.now()
                    }
                    KBRepository.save(kb_data)
                    print(f"Synced KB {kb_id} from ChromaDB")
                except Exception as e:
                    print(f"Error restoring KB {kb_id}: {e}")
    except Exception as e:
        print(f"Error syncing from ChromaDB: {e}")

def init():
    """初始化 KBService"""
    global _initialized
    if not _initialized:
        _sync_from_chroma()
        _initialized = True

class KBService:
    """知识库服务类 - 使用 MySQL 存储"""
    
    @staticmethod
    def create(name: str, description: str = "") -> dict:
        """创建知识库"""
        return KBRepository.create(name, description)

    @staticmethod
    def list_all() -> List[dict]:
        """获取所有知识库"""
        return KBRepository.list_all()

    @staticmethod
    def get_by_id(kb_id: str) -> Optional[dict]:
        """根据ID获取知识库"""
        return KBRepository.get_by_id(kb_id)

    @staticmethod
    def delete(kb_id: str) -> bool:
        """删除知识库"""
        return KBRepository.delete(kb_id)

    @staticmethod
    def update_status(kb_id: str, status: str):
        """更新知识库状态"""
        KBRepository.update_status(kb_id, status)

    @staticmethod
    def update_counts(kb_id: str, doc_count: int, chunk_count: int):
        """更新文档和块计数"""
        KBRepository.update_counts(kb_id, doc_count, chunk_count)

    @staticmethod
    def update(kb_id: str, name: str = None, description: str = None) -> bool:
        """更新知识库"""
        return KBRepository.update(kb_id, name, description)
