"""对话历史 API 集成测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys
sys.path.insert(0, 'src')

from fastapi.testclient import TestClient
from watcher_ai.main import app
from watcher_ai.services.chat_repository import ChatRepository


client = TestClient(app)


class TestListChatHistory:
    """测试 GET /api/knowledge/chat/history"""

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_list_history_returns_user_sessions(self, mock_repo):
        """验证返回用户会话列表"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [
                {'id': 'sess1', 'user_id': 'user123', 'kb_id': 'kb1', 'title': '会话1'},
                {'id': 'sess2', 'user_id': 'user123', 'kb_id': 'kb1', 'title': '会话2'},
            ],
            'total': 2,
            'page': 1,
            'page_size': 20
        }

        response = client.get("/api/knowledge/chat/history?user_id=user123")

        assert response.status_code == 200
        data = response.json()
        assert data['state'] == 0
        assert len(data['data']['sessions']) == 2

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_list_history_sorted_by_updated_at(self, mock_repo):
        """验证按更新时间倒序排列"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [
                {'id': 'sess2', 'updated_at': '2026-05-15T10:00:00'},
                {'id': 'sess1', 'updated_at': '2026-05-15T09:00:00'},
            ],
            'total': 2,
            'page': 1,
            'page_size': 20
        }

        response = client.get("/api/knowledge/chat/history?user_id=user123")

        assert response.status_code == 200
        data = response.json()
        sessions = data['data']['sessions']
        # 验证倒序排列
        for i in range(len(sessions) - 1):
            assert sessions[i]['updated_at'] >= sessions[i + 1]['updated_at']

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_list_history_with_pagination(self, mock_repo):
        """验证分页功能"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [{'id': 'sess3'}],
            'total': 25,
            'page': 2,
            'page_size': 10
        }

        response = client.get("/api/knowledge/chat/history?user_id=user123&page=2&page_size=10")

        assert response.status_code == 200
        data = response.json()
        assert data['data']['page'] == 2
        assert data['data']['page_size'] == 10
        assert data['data']['total'] == 25

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_list_history_empty_user(self, mock_repo):
        """验证空用户返回空列表"""
        mock_repo.list_sessions_by_user.return_value = {
            'sessions': [],
            'total': 0,
            'page': 1,
            'page_size': 20
        }

        response = client.get("/api/knowledge/chat/history?user_id=empty_user")

        assert response.status_code == 200
        data = response.json()
        assert data['data']['sessions'] == []
        assert data['data']['total'] == 0


class TestGetSessionDetail:
    """测试 GET /api/knowledge/chat/sessions/{session_id}"""

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_get_session_detail_returns_info_and_messages(self, mock_repo):
        """验证返回会话信息和消息列表"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess123',
            'user_id': 'user123',
            'kb_id': 'kb1',
            'title': '测试会话',
            'message_count': 2
        }
        mock_repo.list_messages_by_session.return_value = [
            {'id': 1, 'role': 'user', 'content': '你好'},
            {'id': 2, 'role': 'assistant', 'content': '你好！'},
        ]

        response = client.get("/api/knowledge/chat/sessions/sess123?user_id=user123")

        assert response.status_code == 200
        data = response.json()
        assert data['state'] == 0
        assert data['data']['session']['id'] == 'sess123'
        assert len(data['data']['messages']) == 2

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_get_session_not_found(self, mock_repo):
        """验证不存在的会话返回 404"""
        mock_repo.get_session_by_id.return_value = None

        response = client.get("/api/knowledge/chat/sessions/not-exist?user_id=user123")

        assert response.status_code == 404
        assert response.json()['detail'] == 'Session not found'

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_get_session_access_denied(self, mock_repo):
        """验证跨用户访问返回 403"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess999',
            'user_id': 'user123'  # 属于 user123
        }

        response = client.get("/api/knowledge/chat/sessions/sess999?user_id=user456")

        assert response.status_code == 403
        assert response.json()['detail'] == 'Access denied'


class TestDeleteSession:
    """测试 DELETE /api/knowledge/chat/sessions/{session_id}"""

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_delete_session_success(self, mock_repo):
        """验证删除成功"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess789',
            'user_id': 'user123'
        }
        mock_repo.delete_session.return_value = True

        response = client.delete("/api/knowledge/chat/sessions/sess789?user_id=user123")

        assert response.status_code == 200
        data = response.json()
        assert data['state'] == 0
        assert data['data']['message'] == 'deleted'

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_delete_session_not_found(self, mock_repo):
        """验证删除不存在的会话返回 404"""
        mock_repo.get_session_by_id.return_value = None

        response = client.delete("/api/knowledge/chat/sessions/not-exist?user_id=user123")

        assert response.status_code == 404
        assert response.json()['detail'] == 'Session not found'

    @patch('watcher_ai.api.chat_history.ChatRepository')
    def test_delete_session_access_denied(self, mock_repo):
        """验证跨用户删除返回 403"""
        mock_repo.get_session_by_id.return_value = {
            'id': 'sess999',
            'user_id': 'user123'
        }

        response = client.delete("/api/knowledge/chat/sessions/sess999?user_id=user456")

        assert response.status_code == 403
        assert response.json()['detail'] == 'Access denied'


class TestChatWithHistory:
    """测试 POST /api/knowledge/chat/with-history"""

    @patch('watcher_ai.api.chat_history.ChatRepository')
    @patch('watcher_ai.api.chat_history.KBService')
    @patch('watcher_ai.api.chat_history.ChromaService')
    @patch('watcher_ai.api.chat_history.LLMService')
    def test_chat_with_history_creates_session(self, mock_llm, mock_chroma, mock_kb, mock_repo):
        """验证新问答创建会话并保存"""
        mock_repo.create_session.return_value = {'id': 'new_sess', 'user_id': 'user123', 'kb_id': 'kb001'}
        mock_repo.save_message.return_value = 1
        mock_kb.get_by_id.return_value = {'id': 'kb001'}
        mock_chroma.search.return_value = {'documents': [[]]}
        mock_llm.chat.return_value = "这是一个测试回答"

        response = client.post("/api/knowledge/chat/with-history", json={
            "user_id": "user123",
            "kb_id": "kb001",
            "question": "测试问题"
        })

        assert response.status_code == 200
        data = response.json()
        assert data['state'] == 0
        assert data['data']['session_id'] == 'new_sess'
        # 验证保存了两条消息（用户 + 助手）
        assert mock_repo.save_message.call_count == 2

    @patch('watcher_ai.api.chat_history.ChatRepository')
    @patch('watcher_ai.api.chat_history.KBService')
    @patch('watcher_ai.api.chat_history.ChromaService')
    @patch('watcher_ai.api.chat_history.LLMService')
    def test_chat_with_history_saves_user_and_assistant_messages(self, mock_llm, mock_chroma, mock_kb, mock_repo):
        """验证同时保存用户消息和 AI 回复"""
        mock_repo.create_session.return_value = {'id': 'sess', 'user_id': 'user123', 'kb_id': 'kb001'}
        mock_repo.save_message.return_value = 1
        mock_kb.get_by_id.return_value = {'id': 'kb001'}
        mock_chroma.search.return_value = {'documents': [[]]}
        mock_llm.chat.return_value = "AI回答"

        client.post("/api/knowledge/chat/with-history", json={
            "user_id": "user123",
            "kb_id": "kb001",
            "question": "用户问题"
        })

        # 验证保存了用户消息
        calls = mock_repo.save_message.call_args_list
        assert len(calls) == 2
        assert calls[0][0][1] == 'user'  # role
        assert calls[0][0][2] == '用户问题'  # content
        assert calls[1][0][1] == 'assistant'  # role
        assert calls[1][0][2] == 'AI回答'  # content

    @patch('watcher_ai.api.chat_history.ChatRepository')
    @patch('watcher_ai.api.chat_history.KBService')
    @patch('watcher_ai.api.chat_history.ChromaService')
    @patch('watcher_ai.api.chat_history.LLMService')
    def test_chat_with_history_returns_answer_and_session_id(self, mock_llm, mock_chroma, mock_kb, mock_repo):
        """验证返回答案和 session_id"""
        mock_repo.create_session.return_value = {'id': 'sess123', 'user_id': 'user123', 'kb_id': 'kb001'}
        mock_repo.save_message.return_value = 1
        mock_kb.get_by_id.return_value = {'id': 'kb001'}
        mock_chroma.search.return_value = {'documents': [[]]}
        mock_llm.chat.return_value = "这是答案"

        response = client.post("/api/knowledge/chat/with-history", json={
            "user_id": "user123",
            "kb_id": "kb001",
            "question": "问题"
        })

        data = response.json()
        assert 'answer' in data['data']
        assert 'session_id' in data['data']
        assert data['data']['session_id'] == 'sess123'


class TestRouterRegistration:
    """测试路由注册"""

    def test_router_registered_in_app(self):
        """验证 chat_history 路由已注册"""
        from watcher_ai.main import app

        # 获取所有路由路径
        routes = [route.path for route in app.routes]

        # 验证关键端点已注册
        assert any('/chat/history' in path for path in routes), "chat/history 路由未注册"
        assert any('/chat/sessions/' in path for path in routes), "chat/sessions 路由未注册"
        assert any('/chat/with-history' in path for path in routes), "chat/with-history 路由未注册"
