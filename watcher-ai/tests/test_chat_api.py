"""ChatHistory API 集成测试"""
import pytest
from fastapi.testclient import TestClient
from unittest.mock import patch, MagicMock
import sys
sys.path.insert(0, 'src')

from watcher_ai.main import app


class TestChatHistoryAPI:
    """测试 ChatHistory API"""

    client = TestClient(app)

    @patch('watcher_ai.api.chat_history.ChatService')
    def test_get_sessions_list(self, mock_service):
        """测试 GET /api/knowledge/chat/sessions"""
        mock_service.get_user_sessions.return_value = {
            'sessions': [{'id': 'sess1', 'title': '测试'}],
            'total': 1,
            'page': 1,
            'page_size': 20
        }

        response = self.client.get("/api/knowledge/chat/sessions?user_id=user123")

        assert response.status_code == 200
        data = response.json()
        assert data['state'] == 0
        assert len(data['data']['sessions']) == 1

    def test_get_sessions_missing_user_id(self):
        """测试缺少 user_id 参数返回 422"""
        response = self.client.get("/api/knowledge/chat/sessions")

        assert response.status_code == 422

    @patch('watcher_ai.api.chat_history.ChatService')
    def test_get_sessions_pagination(self, mock_service):
        """测试分页参数"""
        mock_service.get_user_sessions.return_value = {
            'sessions': [], 'total': 50, 'page': 2, 'page_size': 10
        }

        response = self.client.get(
            "/api/knowledge/chat/sessions?user_id=user123&page=2&page_size=10"
        )

        assert response.status_code == 200
        mock_service.get_user_sessions.assert_called_once_with("user123", 2, 10)

    @patch('watcher_ai.api.chat_history.ChatService')
    def test_get_session_detail(self, mock_service):
        """测试 GET /api/knowledge/chat/sessions/{session_id}"""
        mock_service.get_session_detail.return_value = {
            'session': {'id': 'sess789', 'title': '测试'},
            'messages': [{'id': 1, 'role': 'user', 'content': '你好'}]
        }

        response = self.client.get("/api/knowledge/chat/sessions/sess789")

        assert response.status_code == 200
        data = response.json()
        assert 'session' in data['data']
        assert 'messages' in data['data']

    @patch('watcher_ai.api.chat_history.ChatService')
    def test_get_session_detail_not_found(self, mock_service):
        """测试 session_id 不存在返回 404"""
        mock_service.get_session_detail.side_effect = ValueError("会话不存在")

        response = self.client.get("/api/knowledge/chat/sessions/not_exist")

        assert response.status_code == 404

    @patch('watcher_ai.api.chat_history.ChatService')
    def test_delete_session(self, mock_service):
        """测试 DELETE /api/knowledge/chat/sessions/{session_id}"""
        mock_service.delete_session.return_value = True

        response = self.client.delete("/api/knowledge/chat/sessions/sess789")

        assert response.status_code == 200
        data = response.json()
        assert data['state'] == 0
        assert data['data']['message'] == 'deleted'

    @patch('watcher_ai.api.chat_history.ChatService')
    def test_delete_session_idempotent(self, mock_service):
        """测试幂等删除"""
        mock_service.delete_session.return_value = True

        response = self.client.delete("/api/knowledge/chat/sessions/not_exist")

        assert response.status_code == 200  # 不返回 404
