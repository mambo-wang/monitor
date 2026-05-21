from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.document_service import DocumentProcessor
from watcher_ai.services.document_store import DocumentStore
from watcher_ai.services.kb_service import KBService
import uuid
import logging

logger = logging.getLogger(__name__)

class BuildService:
    @staticmethod
    def build_knowledge_base(kb_id: str) -> dict:
        """构建知识库"""
        logger.info(f"[BuildService] Starting build for kb_id={kb_id}")
        docs = DocumentStore.list_by_kb(kb_id)
        if not docs:
            raise ValueError("No documents to build")
        logger.info(f"[BuildService] Found {len(docs)} documents to process")
        # 更新状态为 building
        KBService.update_status(kb_id, "building")
        total_chunks = 0
        for doc in docs:
            logger.info(f"[BuildService] Processing doc: {doc['file_name']}, path={doc['file_path']}")
            chunks, err = DocumentProcessor.process_document(doc["file_path"])
            if err:
                logger.error(f"[BuildService] Error processing doc: {err}")
                continue
            if not chunks:
                logger.warning(f"[BuildService] No chunks from doc: {doc['file_name']}")
                continue
            logger.info(f"[BuildService] Got {len(chunks)} chunks from doc")
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
                logger.info(f"[BuildService] Calling ChromaService.add_vectors with {len(documents)} documents")
                ChromaService.add_vectors(kb_id, ids, documents, metadatas)
                logger.info(f"[BuildService] ChromaService.add_vectors completed")
                total_chunks += len(chunks)
                DocumentStore.update_chunk_count(doc["id"], len(chunks))
            except Exception as e:
                logger.error(f"[BuildService] Error adding vectors: {e}")
                raise  # 重新抛出异常以便追踪
        # 更新 KB 状态和计数
        logger.info(f"[BuildService] Updating KB counts: doc_count={len(docs)}, chunk_count={total_chunks}")
        KBService.update_counts(kb_id, len(docs), total_chunks)
        logger.info(f"[BuildService] Updating KB status to ready")
        KBService.update_status(kb_id, "ready")
        logger.info(f"[BuildService] Build completed successfully")
        return {"status": "ready", "chunk_count": total_chunks}
