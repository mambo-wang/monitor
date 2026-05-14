"""对话历史数据库操作层"""
import uuid
import json
from datetime import datetime
from typing import List, Optional, Dict, Any

from watcher_ai.database.mysql_client import MySQLClient


class ChatRepository:
    """对话历史数据库操作类"""

    SESSION_TABLE = "chat_sessions"
    MESSAGE_TABLE = "chat_messages"

    @staticmethod
    def _get_client() -> MySQLClient:
        return MySQLClient()

    @staticmethod
    def create_session(user_id: str, kb_id: str, title: str = "") -> Dict[str, Any]:
        """创建新会话"""
        session_id = str(uuid.uuid4())
        now = datetime.now()

        sql = f"""
            INSERT INTO {ChatRepository.SESSION_TABLE}
            (id, user_id, kb_id, title, message_count, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        params = (session_id, user_id, kb_id, title, 0, now, now)
        ChatRepository._get_client().execute(sql, params)

        return {
            'id': session_id,
            'user_id': user_id,
            'kb_id': kb_id,
            'title': title,
            'message_count': 0,
            'created_at': now,
            'updated_at': now
        }

    @staticmethod
    def get_session_by_id(session_id: str) -> Optional[Dict[str, Any]]:
        """根据 ID 获取会话"""
        sql = f"SELECT * FROM {ChatRepository.SESSION_TABLE} WHERE id = %s"
        return ChatRepository._get_client().query_one(sql, (session_id,))

    @staticmethod
    def save_message(
        session_id: str,
        role: str,
        content: str,
        sources: Optional[List[Dict]] = None
    ) -> int:
        """保存消息"""
        now = datetime.now()
        sources_json = json.dumps(sources) if sources else None

        sql = f"""
            INSERT INTO {ChatRepository.MESSAGE_TABLE}
            (session_id, role, content, sources, created_at)
            VALUES (%s, %s, %s, %s, %s)
        """
        params = (session_id, role, content, sources_json, now)
        ChatRepository._get_client().execute(sql, params)

        result = ChatRepository._get_client().query_one(
            "SELECT LAST_INSERT_ID() as id"
        )
        return result['id']

    @staticmethod
    def list_messages_by_session(session_id: str) -> List[Dict[str, Any]]:
        """查询会话的所有消息"""
        sql = f"""
            SELECT * FROM {ChatRepository.MESSAGE_TABLE}
            WHERE session_id = %s
            ORDER BY created_at ASC
        """
        return ChatRepository._get_client().query_all(sql, (session_id,))

    @staticmethod
    def list_sessions_by_user(
        user_id: str,
        page: int = 1,
        page_size: int = 20
    ) -> Dict[str, Any]:
        """分页查询用户会话"""
        offset = (page - 1) * page_size

        count_sql = f"SELECT COUNT(*) as total FROM {ChatRepository.SESSION_TABLE} WHERE user_id = %s"
        total_result = ChatRepository._get_client().query_one(count_sql, (user_id,))
        total = total_result['total'] if total_result else 0

        sql = f"""
            SELECT * FROM {ChatRepository.SESSION_TABLE}
            WHERE user_id = %s
            ORDER BY updated_at DESC
            LIMIT %s OFFSET %s
        """
        sessions = ChatRepository._get_client().query_all(sql, (user_id, page_size, offset))

        return {
            'sessions': sessions,
            'total': total,
            'page': page,
            'page_size': page_size
        }

    @staticmethod
    def update_message_count(session_id: str, count: int) -> bool:
        """更新会话消息数"""
        sql = f"""
            UPDATE {ChatRepository.SESSION_TABLE}
            SET message_count = %s, updated_at = %s
            WHERE id = %s
        """
        ChatRepository._get_client().execute(sql, (count, datetime.now(), session_id))
        return True

    @staticmethod
    def delete_session(session_id: str) -> bool:
        """删除会话（消息通过外键级联删除）"""
        sql = f"DELETE FROM {ChatRepository.SESSION_TABLE} WHERE id = %s"
        ChatRepository._get_client().execute(sql, (session_id,))
        return True
