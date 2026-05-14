from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.document_service import DocumentProcessor
from watcher_ai.services.document_store import DocumentStore
from watcher_ai.services.kb_service import KBService
import uuid

class BuildService:
    @staticmethod
    def build_knowledge_base(kb_id: str) -> dict:
        """构建知识库"""
        docs = DocumentStore.list_by_kb(kb_id)
        if not docs:
            raise ValueError("No documents to build")
        # 更新状态为 building
        KBService.update_status(kb_id, "building")
        total_chunks = 0
        for doc in docs:
            chunks, err = DocumentProcessor.process_document(doc["file_path"])
            if err:
                continue
            if not chunks:
                continue
            # 添加到 ChromaDB
            ids = [f"{doc['id']}_{i}" for i in range(len(chunks))]
            documents = [c["content"] for c in chunks]
            metadatas = [{
                "kb_id": kb_id,
                "doc_id": doc["id"],
                "file_name": doc["file_name"],
                "chunk_index": c["chunk_index"]
            } for c in chunks]
            try:
                ChromaService.add_vectors(kb_id, ids, documents, metadatas)
                total_chunks += len(chunks)
                DocumentStore.update_chunk_count(doc["id"], len(chunks))
            except Exception as e:
                print(f"Error adding vectors: {e}")
        # 更新 KB 状态和计数
        KBService.update_counts(kb_id, len(docs), total_chunks)
        KBService.update_status(kb_id, "ready")
        return {"status": "ready", "chunk_count": total_chunks}
