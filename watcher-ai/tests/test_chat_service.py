"""ChatService 单元测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys
sys.path.insert(0, 'src')

from watcher_ai.services.chat_service import ChatService


class TestChatServiceCreate:
    """测试 ChatService 创建会话"""

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_create_new_session(self, mock_repo):
        """测试 create_or_append_session() 新建会话"""
        mock_repo.create_session.return_value = {
            'id': 'sess123',
            'user_id': 'user123',
            'kb_id': 'kb456',
            'title': '',
            'message_count': 0
        }
        mock_repo.save_message.return_value = 1
        mock_repo.update_message_count.return_value = True

        session_id, message_id = ChatService.create_or_append_session(
            user_id="user123",
            kb_id="kb456",
            question="什么是云计算",
            session_id=None
        )

        assert session_id == "sess123"
        assert message_id == 1
        mock_repo.create_session.assert_called_once()
        mock_repo.save_message.assert_called_once()

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_append_to_existing_session(self, mock_repo):
        """测试 create_or_append_session() 追加消息"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess789',
            'user_id': 'user123',
            'kb_id': 'kb456',
            'message_count': 1
        }
        mock_repo.save_message.return_value = 2
        mock_repo.update_message_count.return_value = True

        session_id, message_id = ChatService.create_or_append_session(
            user_id="user123",
            kb_id="kb456",
            question="还有哪些特点？",
            session_id="sess789"
        )

        assert session_id == "sess789"
        assert message_id == 2
        mock_repo.get_session_by_id.assert_called_once_with("sess789")

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_cross_user_validation(self, mock_repo):
        """测试续话时跨用户验证"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess789',
            'user_id': 'user123',  # 属于 user123
            'kb_id': 'kb456',
            'message_count': 1
        }

        with pytest.raises(ValueError, match="无权"):
            ChatService.create_or_append_session(
                user_id="user456",  # 不同的用户
                kb_id="kb456",
                question="问题",
                session_id="sess789"
            )


class TestChatServiceQueryDelete:
    """测试 ChatService 查询和删除"""

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_get_user_sessions(self, mock_repo):
        """测试 get_user_sessions() 分页查询"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [{'id': 'sess1'}, {'id': 'sess2'}],
            'total': 2,
            'page': 1,
            'page_size': 20
        }

        result = ChatService.get_user_sessions("user123", page=1, page_size=20)

        assert 'sessions' in result
        assert result['total'] == 2
        mock_repo.list_sessions_by_user.assert_called_once_with("user123", 1, 20)

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_get_user_sessions_defaults(self, mock_repo):
        """测试分页参数默认值"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [], 'total': 0, 'page': 1, 'page_size': 20
        }

        ChatService.get_user_sessions("user123", page=0, page_size=0)

        mock_repo.list_sessions_by_user.assert_called_once_with("user123", 1, 20)

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_get_user_sessions_max_limit(self, mock_repo):
        """测试分页参数上限"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [], 'total': 0, 'page': 1, 'page_size': 100
        }

        ChatService.get_user_sessions("user123", page=1, page_size=1000)

        mock_repo.list_sessions_by_user.assert_called_once_with("user123", 1, 100)  # 超过100限制为100

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_get_session_detail(self, mock_repo):
        """测试 get_session_detail() 获取详情"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess789',
            'user_id': 'user123',
        }
        mock_repo.list_messages_by_session.return_value = [
            {'id': 1, 'role': 'user', 'content': '你好'},
            {'id': 2, 'role': 'assistant', 'content': '你好！'},
        ]

        result = ChatService.get_session_detail("sess789")

        assert 'session' in result
        assert 'messages' in result
        assert len(result['messages']) == 2

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_get_session_detail_not_exists(self, mock_repo):
        """测试 get_session_detail() 会话不存在"""
        mock_repo.get_session_by_id.return_value = None

        with pytest.raises(ValueError, match="会话不存在"):
            ChatService.get_session_detail("not_exist")

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_delete_session_success(self, mock_repo):
        """测试 delete_session() 成功删除"""
        mock_repo.delete_session.return_value = True

        result = ChatService.delete_session("sess789")

        assert result is True
        mock_repo.delete_session.assert_called_once_with("sess789")

    @patch('watcher_ai.services.chat_service.ChatRepository')
    def test_delete_session_idempotent(self, mock_repo):
        """测试 delete_session() 幂等删除"""
        mock_repo.delete_session.return_value = True

        result = ChatService.delete_session("not_exist")

        assert result is True  # 不抛异常
