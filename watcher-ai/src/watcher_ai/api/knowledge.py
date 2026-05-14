from fastapi import APIRouter, HTTPException, UploadFile, File
from pydantic import BaseModel
from typing import List, Optional, Any
from watcher_ai.models.schemas import KnowledgeBaseCreate, KnowledgeBaseResponse
from watcher_ai.services.kb_service import KBService
from watcher_ai.services.document_store import DocumentStore
from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.build_service import BuildService
from watcher_ai.services.llm_service import LLMService
from watcher_ai.services.chat_service import ChatService
from watcher_ai.services.chat_repository import ChatRepository
import os

router = APIRouter(prefix="/api/knowledge", tags=["knowledge"])

ALLOWED_EXTENSIONS = {".pdf", ".md", ".txt"}

# Request/Response 模型
class ChatRequest(BaseModel):
    kb_id: str
    question: str
    history: Optional[List[dict]] = None
    user_id: Optional[str] = None
    session_id: Optional[str] = None

class SearchRequest(BaseModel):
    query: str
    top_k: int = 3

def ok_response(data: Any):
    """包装成功响应，匹配前端格式"""
    return {"state": 0, "data": data}

@router.post("/kbs", status_code=201)
def create_kb(req: KnowledgeBaseCreate):
    kb = KBService.create(req.name, req.description)
    return ok_response(kb)

@router.get("/kbs")
def list_kbs():
    return ok_response(KBService.list_all())

@router.get("/kbs/{kb_id}")
def get_kb(kb_id: str):
    kb = KBService.get_by_id(kb_id)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    return ok_response(kb)

@router.delete("/kbs/{kb_id}")
def delete_kb(kb_id: str):
    if not KBService.delete(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    return ok_response({"message": "deleted"})

# 文档管理
@router.post("/kbs/{kb_id}/documents", status_code=201)
async def upload_document(kb_id: str, file: UploadFile = File(...)):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    ext = os.path.splitext(file.filename)[1].lower()
    if ext not in ALLOWED_EXTENSIONS:
        raise HTTPException(status_code=400, detail=f"Unsupported file format: {ext}")
    content = await file.read()
    doc = DocumentStore.save_file(kb_id, file.filename, content)
    return ok_response(doc)

@router.get("/kbs/{kb_id}/documents")
def list_documents(kb_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    return ok_response(DocumentStore.list_by_kb(kb_id))

@router.delete("/kbs/{kb_id}/documents/{doc_id}")
def delete_document(kb_id: str, doc_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    if not DocumentStore.delete(doc_id):
        raise HTTPException(status_code=404, detail="Document not found")
    return ok_response({"message": "deleted"})

# 构建与统计
@router.post("/kbs/{kb_id}/build")
def build_kb(kb_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    try:
        result = BuildService.build_knowledge_base(kb_id)
        return ok_response(result)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        KBService.update_status(kb_id, "error")
        raise HTTPException(status_code=500, detail=str(e))

@router.get("/kbs/{kb_id}/stats")
def get_kb_stats(kb_id: str):
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    kb = KBService.get_by_id(kb_id)
    chunk_count = ChromaService.get_count(kb_id)
    return ok_response({
        "document_count": kb["document_count"],
        "chunk_count": chunk_count,
        "status": kb["status"]
    })

# RAG 问答
@router.post("/chat")
def chat(req: ChatRequest):
    """RAG 问答"""
    kb = KBService.get_by_id(req.kb_id)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    
    chunk_count = ChromaService.get_count(req.kb_id)
    if chunk_count == 0:
        raise HTTPException(status_code=400, detail="Knowledge base is empty, please build first")
    
    results = ChromaService.search(req.kb_id, req.question, top_k=3)
    
    if not results.get("documents") or not results["documents"][0]:
        # 即使没有检索结果也保存对话
        if req.user_id:
            session_id, message_id = ChatService.create_or_append_session(
                user_id=req.user_id,
                kb_id=req.kb_id,
                question=req.question,
                session_id=req.session_id
            )
            return ok_response({
                "answer": "没有找到相关文档",
                "sources": [],
                "session_id": session_id,
                "message_id": message_id
            })
        return ok_response({"answer": "没有找到相关文档", "sources": []})
    
    context_parts = []
    sources = []
    for doc, meta, dist in zip(
        results["documents"][0],
        results["metadatas"][0],
        results["distances"][0]
    ):
        context_parts.append(f"[{meta.get('file_name', 'unknown')}]: {doc}")
        sources.append({
            "content": doc[:200],
            "file_name": meta.get("file_name", "unknown"),
            "score": 1 - dist
        })
    
    context = "\n\n".join(context_parts)
    answer = LLMService.chat(req.question, context)
    
    # 保存对话历史
    session_id = req.session_id
    message_id = None
    if req.user_id:
        session_id, message_id = ChatService.create_or_append_session(
            user_id=req.user_id,
            kb_id=req.kb_id,
            question=req.question,
            session_id=req.session_id
        )
        # 保存助手回复
        ChatRepository.save_message(
            session_id=session_id,
            role="assistant",
            content=answer,
            sources=sources
        )
        # 更新消息数
        session = ChatRepository.get_session_by_id(session_id)
        ChatRepository.update_message_count(session_id, session['message_count'] + 1)
    
    return ok_response({
        "answer": answer,
        "sources": sources,
        "session_id": session_id,
        "message_id": message_id
    })

@router.post("/kbs/{kb_id}/search")
def search_kb(kb_id: str, req: SearchRequest):
    """检索知识库"""
    if not KBService.get_by_id(kb_id):
        raise HTTPException(status_code=404, detail="Knowledge base not found")
    
    results = ChromaService.search(kb_id, req.query, top_k=req.top_k)
    
    documents = []
    for doc, meta, dist in zip(
        results.get("documents", [[]])[0] if results.get("documents") else [],
        results.get("metadatas", [[]])[0] if results.get("metadatas") else [],
        results.get("distances", [[]])[0] if results.get("distances") else []
    ):
        documents.append({
            "content": doc,
            "file_name": meta.get("file_name", "unknown"),
            "chunk_index": meta.get("chunk_index", 0),
            "score": 1 - dist
        })
    
    return ok_response({"documents": documents})
