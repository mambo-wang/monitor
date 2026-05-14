from typing import Dict, List, Optional
from datetime import datetime
import uuid
import os
import shutil

UPLOAD_DIR = os.path.join(os.path.dirname(__file__), "../../uploads")

class DocumentStore:
    _doc_store: Dict[str, dict] = {}
    _kb_docs: Dict[str, List[str]] = {}  # kb_id -> [doc_ids]

    @staticmethod
    def init():
        os.makedirs(UPLOAD_DIR, exist_ok=True)

    @staticmethod
    def save_file(kb_id: str, filename: str, content: bytes) -> dict:
        kb_id_safe = kb_id.replace("-", "_")
        kb_dir = os.path.join(UPLOAD_DIR, kb_id_safe)
        os.makedirs(kb_dir, exist_ok=True)
        file_path = os.path.join(kb_dir, filename)
        with open(file_path, "wb") as f:
            f.write(content)
        doc_id = str(uuid.uuid4())
        doc = {
            "id": doc_id,
            "kb_id": kb_id,
            "file_name": filename,
            "file_path": file_path,
            "file_size": len(content),
            "status": "pending",
            "chunk_count": 0,
            "created_at": datetime.now()
        }
        DocumentStore._doc_store[doc_id] = doc
        if kb_id not in DocumentStore._kb_docs:
            DocumentStore._kb_docs[kb_id] = []
        DocumentStore._kb_docs[kb_id].append(doc_id)
        return doc

    @staticmethod
    def list_by_kb(kb_id: str) -> List[dict]:
        doc_ids = DocumentStore._kb_docs.get(kb_id, [])
        return [DocumentStore._doc_store[did] for did in doc_ids if did in DocumentStore._doc_store]

    @staticmethod
    def get_by_id(doc_id: str) -> Optional[dict]:
        return DocumentStore._doc_store.get(doc_id)

    @staticmethod
    def delete(doc_id: str) -> bool:
        doc = DocumentStore._doc_store.get(doc_id)
        if not doc:
            return False
        # 删除文件
        if os.path.exists(doc["file_path"]):
            os.remove(doc["file_path"])
        # 从 kb_docs 中移除
        kb_id = doc["kb_id"]
        if kb_id in DocumentStore._kb_docs and doc_id in DocumentStore._kb_docs[kb_id]:
            DocumentStore._kb_docs[kb_id].remove(doc_id)
        del DocumentStore._doc_store[doc_id]
        return True

    @staticmethod
    def update_chunk_count(doc_id: str, chunk_count: int):
        if doc_id in DocumentStore._doc_store:
            DocumentStore._doc_store[doc_id]["chunk_count"] = chunk_count
            DocumentStore._doc_store[doc_id]["status"] = "parsed"
