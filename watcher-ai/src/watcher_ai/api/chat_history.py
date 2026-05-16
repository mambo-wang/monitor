"""对话历史 API 路由"""
from fastapi import APIRouter, HTTPException, Query
from pydantic import BaseModel
from typing import List, Optional, Dict, Any

from watcher_ai.services.chat_repository import ChatRepository
from watcher_ai.services.kb_service import KBService
from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.llm_service import LLMService

router = APIRouter(prefix="/api/knowledge/chat", tags=["chat-history"])


def ok_response(data: Any):
    """包装成功响应"""
    return {"state": 0, "data": data}


@router.get("/history")
def list_chat_history(
    user_id: str = Query(None, description="用户ID"),
    kb_id: str = Query(None, description="知识库ID"),
    page: int = Query(1, ge=1, description="页码"),
    page_size: int = Query(20, ge=1, le=100, description="每页数量")
):
    """获取对话历史列表（按用户或知识库筛选）"""
    if kb_id:
        result = ChatRepository.list_sessions_by_kb(kb_id, page, page_size)
    elif user_id:
        result = ChatRepository.list_sessions_by_user(user_id, page, page_size)
    else:
        raise HTTPException(status_code=400, detail="user_id or kb_id is required")
    return ok_response(result)


@router.get("/sessions/{session_id}")
def get_session_detail(
    session_id: str,
    user_id: str = Query(..., description="用户ID")
):
    """获取会话详情及消息"""
    session = ChatRepository.get_session_by_id(session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Session not found")
    if session['user_id'] != user_id:
        raise HTTPException(status_code=403, detail="Access denied")
    messages = ChatRepository.list_messages_by_session(session_id)
    return ok_response({
        'session': session,
        'messages': messages
    })


@router.delete("/sessions/{session_id}")
def delete_session(
    session_id: str,
    user_id: str = Query(..., description="用户ID")
):
    """删除对话会话"""
    session = ChatRepository.get_session_by_id(session_id)
    if not session:
        raise HTTPException(status_code=404, detail="Session not found")
    if session['user_id'] != user_id:
        raise HTTPException(status_code=403, detail="Access denied")
    ChatRepository.delete_session(session_id)
    return ok_response({"message": "deleted"})


class ChatWithHistoryRequest(BaseModel):
    """带历史的问答请求"""
    user_id: str
    kb_id: str
    question: str
    session_id: Optional[str] = None


@router.post("/with-history")
def chat_with_history(req: ChatWithHistoryRequest):
    """带历史的 RAG 问答，自动保存记录"""
    # 1. 获取或创建会话
    if req.session_id:
        session = ChatRepository.get_session_by_id(req.session_id)
        if not session or session['user_id'] != req.user_id:
            raise HTTPException(status_code=403, detail="Invalid session")
    else:
        session = ChatRepository.create_session(req.user_id, req.kb_id)

    # 2. 执行 RAG 问答
    kb = KBService.get_by_id(req.kb_id)
    if not kb:
        raise HTTPException(status_code=404, detail="Knowledge base not found")

    chunk_count = ChromaService.get_count(req.kb_id)
    if chunk_count == 0:
        raise HTTPException(status_code=400, detail="Knowledge base is empty, please build first")

    results = ChromaService.search(req.kb_id, req.question, top_k=3)

    if not results.get("documents") or not results["documents"][0]:
        answer = "没有找到相关文档"
    else:
        context_parts = []
        for doc in results["documents"][0]:
            context_parts.append(doc)
        context = "\n\n".join(context_parts)
        answer = LLMService.chat(req.question, context)

    # 3. 保存消息
    ChatRepository.save_message(session['id'], "user", req.question)
    ChatRepository.save_message(session['id'], "assistant", answer)

    # 4. 更新消息计数
    messages = ChatRepository.list_messages_by_session(session['id'])
    ChatRepository.update_message_count(session['id'], len(messages))

    return ok_response({
        "answer": answer,
        "session_id": session['id']
    })
