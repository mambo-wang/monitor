"""ChatRepository 测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys
sys.path.insert(0, 'src')

from watcher_ai.services.chat_repository import ChatRepository


class TestChatRepositoryBasic:
    """测试 ChatRepository 基础 CRUD"""

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_create_session_returns_correct_fields(self, mock_mysql):
        """测试 create_session() 创建会话返回正确字段"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.execute.return_value = 1

        result = ChatRepository.create_session(
            user_id="user123",
            kb_id="kb456",
            title="测试对话"
        )

        assert 'id' in result
        assert result['user_id'] == "user123"
        assert result['kb_id'] == "kb456"
        assert result['title'] == "测试对话"
        assert result['message_count'] == 0
        assert 'created_at' in result
        assert 'updated_at' in result

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_get_session_by_id_exists(self, mock_mysql):
        """测试 get_session_by_id() 查询存在的会话"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_one.return_value = {
            'id': 'sess789',
            'user_id': 'user123',
            'kb_id': 'kb456',
            'title': '测试',
            'message_count': 0
        }

        result = ChatRepository.get_session_by_id("sess789")

        assert result is not None
        assert result['id'] == 'sess789'

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_get_session_by_id_not_exists(self, mock_mysql):
        """测试 get_session_by_id() 查询不存在的会话返回 None"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_one.return_value = None

        result = ChatRepository.get_session_by_id("not_exist")

        assert result is None


class TestChatRepositoryMessages:
    """测试 ChatRepository 消息操作"""

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_save_user_message(self, mock_mysql):
        """测试 save_message() 保存用户消息"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.execute.return_value = 1
        mock_client.query_one.return_value = {'id': 1}

        result = ChatRepository.save_message(
            session_id="sess789",
            role="user",
            content="你好"
        )

        assert result is not None
        mock_client.execute.assert_called_once()

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_save_assistant_message_with_sources(self, mock_mysql):
        """测试 save_message() 保存助手消息并存储 sources"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.execute.return_value = 1
        mock_client.query_one.return_value = {'id': 2}

        sources = [{"file": "doc.pdf"}]
        result = ChatRepository.save_message(
            session_id="sess789",
            role="assistant",
            content="你好！有什么可以帮助你的吗？",
            sources=sources
        )

        assert result is not None

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_list_messages_by_session(self, mock_mysql):
        """测试 list_messages_by_session() 查询消息列表"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_all.return_value = [
            {'id': 1, 'session_id': 'sess789', 'role': 'user', 'content': '你好', 'sources': None},
            {'id': 2, 'session_id': 'sess789', 'role': 'assistant', 'content': '你好！', 'sources': None},
        ]

        result = ChatRepository.list_messages_by_session("sess789")

        assert len(result) == 2
        assert result[0]['role'] == 'user'
        assert result[1]['role'] == 'assistant'


class TestChatRepositoryPaginationDelete:
    """测试 ChatRepository 分页查询和删除"""

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_list_sessions_by_user_pagination(self, mock_mysql):
        """测试 list_sessions_by_user() 分页查询"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_all.return_value = [
            {'id': 'sess1', 'user_id': 'user123', 'kb_id': 'kb1', 'title': '会话1'},
            {'id': 'sess2', 'user_id': 'user123', 'kb_id': 'kb1', 'title': '会话2'},
        ]
        mock_client.query_one.return_value = {'total': 25}

        result = ChatRepository.list_sessions_by_user("user123", page=1, page_size=10)

        assert 'sessions' in result
        assert 'total' in result
        assert len(result['sessions']) == 2
        assert result['total'] == 25

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_list_sessions_by_user_empty(self, mock_mysql):
        """测试 list_sessions_by_user() 空结果"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_all.return_value = []
        mock_client.query_one.return_value = {'total': 0}

        result = ChatRepository.list_sessions_by_user("empty_user")

        assert result['sessions'] == []
        assert result['total'] == 0

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_update_message_count(self, mock_mysql):
        """测试 update_message_count() 更新消息数"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.execute.return_value = 1

        result = ChatRepository.update_message_count("sess789", 5)

        assert result is True
        mock_client.execute.assert_called_once()

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_delete_session_exists(self, mock_mysql):
        """测试 delete_session() 删除存在的会话"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.execute.return_value = 1

        result = ChatRepository.delete_session("sess789")

        assert result is True

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_delete_session_not_exists(self, mock_mysql):
        """测试 delete_session() 幂等删除"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.execute.return_value = 0

        result = ChatRepository.delete_session("not_exist")

        assert result is True  # 幂等性


class TestListSessionsByKb:
    """测试按知识库查询会话列表"""

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_list_sessions_by_kb_returns_correct_list(self, mock_mysql):
        """测试 kb_id 有会话时返回正确列表"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_all.return_value = [
            {'id': 'sess1', 'user_id': 'user123', 'kb_id': 'kb001', 'title': '会话1'},
            {'id': 'sess2', 'user_id': 'user456', 'kb_id': 'kb001', 'title': '会话2'},
        ]
        mock_client.query_one.return_value = {'total': 2}

        result = ChatRepository.list_sessions_by_kb("kb001", page=1, page_size=20)
        assert result['total'] == 2
        assert len(result['sessions']) == 2

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_list_sessions_by_kb_returns_empty_list(self, mock_mysql):
        """测试 kb_id 无会话时返回空列表"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_all.return_value = []
        mock_client.query_one.return_value = {'total': 0}

        result = ChatRepository.list_sessions_by_kb("kb-empty", page=1, page_size=20)
        assert result['total'] == 0
        assert len(result['sessions']) == 0

    @patch('watcher_ai.services.chat_repository.MySQLClient')
    def test_list_sessions_by_kb_pagination(self, mock_mysql):
        """测试分页参数正确"""
        mock_client = MagicMock()
        mock_mysql.return_value = mock_client
        mock_client.query_all.return_value = [
            {'id': 'sess1', 'user_id': 'user123', 'kb_id': 'kb001', 'title': '会话1'},
        ]
        mock_client.query_one.return_value = {'total': 2}

        result = ChatRepository.list_sessions_by_kb("kb001", page=1, page_size=1)
        assert len(result['sessions']) == 1
        assert result['total'] == 2
        assert result['page'] == 1
        assert result['page_size'] == 1
