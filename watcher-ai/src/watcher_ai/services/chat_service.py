"""对话历史业务逻辑层"""
from typing import Tuple, Optional, Dict, Any, List

from watcher_ai.services.chat_repository import ChatRepository


class ChatService:
    """对话历史业务逻辑类"""

    @staticmethod
    def create_or_append_session(
        user_id: str,
        kb_id: str,
        question: str,
        session_id: Optional[str] = None
    ) -> Tuple[str, int]:
        """
        创建新会话或追加消息到已有会话
        返回: (session_id, message_id)
        """
        if session_id:
            # 追加到已有会话
            session = ChatRepository.get_session_by_id(session_id)
            if not session:
                raise ValueError(f"会话不存在: {session_id}")

            # 跨用户验证
            if session['user_id'] != user_id:
                raise ValueError("无权操作此会话")

            # 保存用户消息
            message_id = ChatRepository.save_message(
                session_id=session_id,
                role="user",
                content=question
            )

            # 更新消息数
            new_count = session.get('message_count', 0) + 1
            ChatRepository.update_message_count(session_id, new_count)
        else:
            # 创建新会话
            title = question[:20] if len(question) > 20 else question
            session = ChatRepository.create_session(
                user_id=user_id,
                kb_id=kb_id,
                title=title
            )
            session_id = session['id']

            # 保存用户消息
            message_id = ChatRepository.save_message(
                session_id=session_id,
                role="user",
                content=question
            )

            # 更新消息数
            ChatRepository.update_message_count(session_id, 1)

        return session_id, message_id

    @staticmethod
    def get_user_sessions(
        user_id: str,
        page: int = 1,
        page_size: int = 20
    ) -> Dict[str, Any]:
        """获取用户会话列表"""
        # 参数校验和默认值
        page = max(1, page) if page > 0 else 1
        page_size = min(max(1, page_size), 100) if page_size > 0 else 20

        return ChatRepository.list_sessions_by_user(user_id, page, page_size)

    @staticmethod
    def get_session_detail(session_id: str) -> Dict[str, Any]:
        """获取会话详情（含消息）"""
        session = ChatRepository.get_session_by_id(session_id)
        if not session:
            raise ValueError(f"会话不存在: {session_id}")

        messages = ChatRepository.list_messages_by_session(session_id)

        return {
            'session': session,
            'messages': messages
        }

    @staticmethod
    def delete_session(session_id: str) -> bool:
        """删除会话（幂等）"""
        return ChatRepository.delete_session(session_id)
