"""MCP RAG 工具服务"""
from typing import Optional, Dict, Any
from pydantic import BaseModel

from watcher_ai.services.kb_service import KBService
from watcher_ai.services.chroma_service import ChromaService
from watcher_ai.services.chat_repository import ChatRepository
from watcher_ai.services.llm_service import LLMService


class McpRagRequest(BaseModel):
    """MCP RAG 请求模型"""
    kb_id: Optional[str] = None
    question: str
    session_id: Optional[str] = None


def _ok_response(data: Any):
    return {"state": 0, "data": data}


def rag_chat(request: McpRagRequest) -> Dict[str, Any]:
    """RAG 问答 MCP 工具"""
    if not request.kb_id and not request.session_id:
        raise ValueError("kb_id or session_id is required")

    # 1. 获取或创建会话
    if request.session_id:
        session = ChatRepository.get_session_by_id(request.session_id)
        if not session:
            raise ValueError(f"Session {request.session_id} not found")
        kb_id = session['kb_id']
    else:
        kb_id = request.kb_id
        # 检查 kb 是否存在
        kb = KBService.get_by_id(kb_id)
        if not kb:
            raise ValueError(f"Knowledge base {kb_id} not found")
        # 创建新会话
        session = ChatRepository.create_session(
            user_id="mcp_system",
            kb_id=kb_id,
            title=request.question[:50]  # 用问题前50字符作为标题
        )

    # 2. 执行 RAG 问答
    chunk_count = ChromaService.get_count(kb_id)
    if chunk_count == 0:
        return _ok_response({
            "answer": "Knowledge base is empty, please build first",
            "session_id": session['id']
        })

    results = ChromaService.search(kb_id, request.question, top_k=3)

    if not results.get("documents") or not results["documents"][0]:
        answer = "没有找到相关文档"
    else:
        context_parts = []
        for doc in results["documents"][0]:
            context_parts.append(doc)
        context = "\n\n".join(context_parts)
        answer = LLMService.chat(request.question, context)

    # 3. 保存消息
    ChatRepository.save_message(session['id'], "user", request.question)
    ChatRepository.save_message(session['id'], "assistant", answer)

    # 4. 更新消息计数
    messages = ChatRepository.list_messages_by_session(session['id'])
    ChatRepository.update_message_count(session['id'], len(messages))

    return _ok_response({
        "answer": answer,
        "session_id": session['id']
    })


def list_sessions(kb_id: str) -> Dict[str, Any]:
    """列出知识库下的所有会话 MCP 工具"""
    result = ChatRepository.list_sessions_by_kb(kb_id, page=1, page_size=100)
    return _ok_response(result)


def delete_session(session_id: str) -> Dict[str, Any]:
    """删除会话 MCP 工具"""
    session = ChatRepository.get_session_by_id(session_id)
    if not session:
        raise ValueError(f"Session {session_id} not found")
    ChatRepository.delete_session(session_id)
    return _ok_response({"message": "deleted"})