"""MCP RAG 工具测试"""
import pytest
from unittest.mock import MagicMock, patch
import sys
sys.path.insert(0, 'src')

from watcher_ai.api.mcp_rag import rag_chat, McpRagRequest, list_sessions, delete_session


class TestRagChat:
    """测试 rag_chat MCP 工具"""

    @patch('watcher_ai.api.mcp_rag.LLMService')
    @patch('watcher_ai.api.mcp_rag.ChromaService')
    @patch('watcher_ai.api.mcp_rag.ChatRepository')
    @patch('watcher_ai.api.mcp_rag.KBService')
    def test_rag_chat_with_kb_id_and_question(self, mock_kb, mock_chroma, mock_chat_repo, mock_llm):
        """测试 kb_id + question 返回问答结果"""
        # GIVEN: kb_id="kb001", question="CAS虚拟机状态有哪些？"
        mock_kb.get_by_id.return_value = {'id': 'kb001', 'name': 'KB1'}
        mock_chroma.get_count.return_value = 10
        mock_chroma.search.return_value = {
            'documents': [['相关文档内容1', '相关文档内容2']]
        }
        mock_chat_repo.create_session.return_value = {'id': 'sess_new', 'kb_id': 'kb001'}
        mock_chat_repo.list_messages_by_session.return_value = []
        mock_llm.chat.return_value = "CAS虚拟机状态有：RUNNING, PAUSED, SHUTDOWN"

        request = McpRagRequest(kb_id="kb001", question="CAS虚拟机状态有哪些？")
        result = rag_chat(request)

        # THEN: 返回包含 answer 的响应
        assert 'answer' in result['data']
        assert 'session_id' in result['data']
        assert result['data']['answer'] == "CAS虚拟机状态有：RUNNING, PAUSED, SHUTDOWN"

    @patch('watcher_ai.api.mcp_rag.LLMService')
    @patch('watcher_ai.api.mcp_rag.ChromaService')
    @patch('watcher_ai.api.mcp_rag.ChatRepository')
    def test_rag_chat_with_session_id_continues_session(self, mock_chat_repo, mock_chroma, mock_llm):
        """测试 session_id 继续会话"""
        # GIVEN: session_id="sess001" 已存在
        mock_chat_repo.get_session_by_id.return_value = {
            'id': 'sess001', 'kb_id': 'kb001', 'user_id': 'user123'
        }
        mock_chroma.get_count.return_value = 10
        mock_chroma.search.return_value = {
            'documents': [['相关文档内容']]
        }
        mock_chat_repo.list_messages_by_session.return_value = []
        mock_llm.chat.return_value = "那如何关机？"

        request = McpRagRequest(session_id="sess001", question="那如何关机？")
        result = rag_chat(request)

        # THEN: 返回的 session_id 与传入一致
        assert result['data']['session_id'] == "sess001"

    def test_rag_chat_without_kb_id_and_session_id_raises_error(self):
        """测试缺少 kb_id 且无 session_id 时报错"""
        # GIVEN: request 只有 question，没有 kb_id 和 session_id
        request = McpRagRequest(question="CAS虚拟机状态有哪些？")

        # THEN: 抛出 ValueError
        with pytest.raises(ValueError, match="kb_id or session_id is required"):
            rag_chat(request)


class TestListSessions:
    """测试 list_sessions MCP 工具"""

    @patch('watcher_ai.api.mcp_rag.ChatRepository')
    def test_list_sessions_returns_kb_sessions(self, mock_chat_repo):
        """测试传入 kb_id 返回该知识库的会话列表"""
        # GIVEN: kb_id="kb001" 有 2 个会话
        mock_chat_repo.list_sessions_by_kb.return_value = {
            'sessions': [
                {'id': 'sess1', 'title': '会话1'},
                {'id': 'sess2', 'title': '会话2'},
            ],
            'total': 2,
            'page': 1,
            'page_size': 100
        }

        result = list_sessions(kb_id="kb001")

        # THEN: 返回 sessions 列表，total=2
        assert 'sessions' in result['data']
        assert len(result['data']['sessions']) == 2


class TestDeleteSession:
    """测试 delete_session MCP 工具"""

    @patch('watcher_ai.api.mcp_rag.ChatRepository')
    def test_delete_session_deletes_session(self, mock_chat_repo):
        """测试传入 session_id 删除会话"""
        # GIVEN: session_id="sess001" 已存在
        mock_chat_repo.get_session_by_id.return_value = {
            'id': 'sess001', 'kb_id': 'kb001'
        }
        mock_chat_repo.delete_session.return_value = True

        result = delete_session(session_id="sess001")

        # THEN: 返回删除成功确认
        assert result['state'] == 0
        assert result['data']['message'] == 'deleted'
        mock_chat_repo.delete_session.assert_called_once_with("sess001")

    @patch('watcher_ai.api.mcp_rag.ChatRepository')
    def test_delete_session_not_found_raises_error(self, mock_chat_repo):
        """测试删除不存在的 session_id 报错"""
        # GIVEN: session_id="not-exist-session" 不存在
        mock_chat_repo.get_session_by_id.return_value = None

        with pytest.raises(ValueError, match="Session .* not found"):
            delete_session(session_id="not-exist-session")