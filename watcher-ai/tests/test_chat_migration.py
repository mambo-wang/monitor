"""数据库迁移测试 - 验证 chat_sessions 和 chat_messages 表结构"""
import pytest
import sys
sys.path.insert(0, 'src')

from watcher_ai.database.mysql_client import MySQLClient


class TestChatMigration:
    """测试对话历史表结构"""

    def test_chat_sessions_table_exists(self):
        """验证 chat_sessions 表存在且结构正确"""
        client = MySQLClient()
        sql = """
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = 'chat_sessions' AND TABLE_SCHEMA = DATABASE()
            ORDER BY ORDINAL_POSITION
        """
        columns = client.query_all(sql)
        column_names = [col['COLUMN_NAME'] for col in columns]

        # 验证必需字段
        assert 'id' in column_names, "chat_sessions 表缺少 id 字段"
        assert 'user_id' in column_names, "chat_sessions 表缺少 user_id 字段"
        assert 'kb_id' in column_names, "chat_sessions 表缺少 kb_id 字段"
        assert 'title' in column_names, "chat_sessions 表缺少 title 字段"
        assert 'message_count' in column_names, "chat_sessions 表缺少 message_count 字段"
        assert 'created_at' in column_names, "chat_sessions 表缺少 created_at 字段"
        assert 'updated_at' in column_names, "chat_sessions 表缺少 updated_at 字段"

        # 验证索引
        indexes = client.query_all("SHOW INDEX FROM chat_sessions")
        index_names = [idx['Key_name'] for idx in indexes]
        assert 'idx_user_id' in index_names or any('user_id' in idx for idx in indexes if idx['Key_name'] == 'PRIMARY'), \
            "chat_sessions 表缺少 user_id 索引"

    def test_chat_messages_table_exists(self):
        """验证 chat_messages 表存在且结构正确"""
        client = MySQLClient()
        sql = """
            SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_KEY
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_NAME = 'chat_messages' AND TABLE_SCHEMA = DATABASE()
            ORDER BY ORDINAL_POSITION
        """
        columns = client.query_all(sql)
        column_names = [col['COLUMN_NAME'] for col in columns]

        # 验证必需字段
        assert 'id' in column_names, "chat_messages 表缺少 id 字段"
        assert 'session_id' in column_names, "chat_messages 表缺少 session_id 字段"
        assert 'role' in column_names, "chat_messages 表缺少 role 字段"
        assert 'content' in column_names, "chat_messages 表缺少 content 字段"
        assert 'sources' in column_names, "chat_messages 表缺少 sources 字段"
        assert 'created_at' in column_names, "chat_messages 表缺少 created_at 字段"

        # 验证外键
        foreign_keys = client.query_all("""
            SELECT CONSTRAINT_NAME, REFERENCED_TABLE_NAME
            FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
            WHERE TABLE_NAME = 'chat_messages' AND REFERENCED_TABLE_NAME IS NOT NULL
        """)
        assert any(fk['REFERENCED_TABLE_NAME'] == 'chat_sessions' for fk in foreign_keys), \
            "chat_messages 表缺少指向 chat_sessions 的外键"

    def test_foreign_key_cascade_delete(self):
        """验证删除会话时消息级联删除"""
        client = MySQLClient()

        # 创建测试会话
        import uuid
        session_id = str(uuid.uuid4())

        # 插入测试会话和消息
        client.execute("""
            INSERT INTO chat_sessions (id, user_id, kb_id, title, message_count, created_at, updated_at)
            VALUES (%s, 'test_user', 'test_kb', 'test', 1, NOW(), NOW())
        """, (session_id,))

        client.execute("""
            INSERT INTO chat_messages (session_id, role, content, created_at)
            VALUES (%s, 'user', 'test message', NOW())
        """, (session_id,))

        # 验证消息存在
        msg_count_before = client.query_one(
            "SELECT COUNT(*) as cnt FROM chat_messages WHERE session_id = %s",
            (session_id,)
        )
        assert msg_count_before['cnt'] > 0, "测试消息未插入成功"

        # 删除会话（应该级联删除消息）
        client.execute("DELETE FROM chat_sessions WHERE id = %s", (session_id,))

        # 验证消息已被级联删除
        msg_count_after = client.query_one(
            "SELECT COUNT(*) as cnt FROM chat_messages WHERE session_id = %s",
            (session_id,)
        )
        assert msg_count_after['cnt'] == 0, "外键级联删除未生效"
