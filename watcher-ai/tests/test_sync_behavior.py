"""ChromaDB同步行为测试"""
import pytest
import sys
import os
from datetime import datetime
from unittest.mock import Mock, patch, MagicMock

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

class MockChromaCollection:
    """模拟 ChromaDB Collection"""
    def __init__(self, name, count=0):
        self.name = name
        self._count = count
    
    def count(self):
        return self._count

class MockChromaClient:
    """模拟 ChromaDB Client"""
    def __init__(self, collections=None):
        self._collections = collections or []
    
    def list_collections(self):
        return self._collections
    
    def get_collection(self, name):
        for col in self._collections:
            if col.name == name:
                return col
        raise ValueError(f"Collection {name} not found")

class TestSyncBehavior:
    """同步行为测试类"""
    
    def test_sync_does_not_overwrite_existing_kb_name(self):
        """验证同步时不覆盖 MySQL 中已有的知识库名称"""
        from watcher_ai.database.mysql_client import MySQLClient
        MySQLClient._instance = None
        
        from watcher_ai.services.kb_repository import KBRepository
        from watcher_ai.services.kb_service import _sync_from_chroma
        
        client = MySQLClient()
        
        # 清理测试数据
        client.execute("DELETE FROM knowledge_bases WHERE id = %s", ("existing-kb-123",))
        
        try:
            # 模拟 MySQL 中已有知识库，用户自定义名称为 "我的知识库"
            now = datetime.now()
            client.execute(
                """INSERT INTO knowledge_bases 
                   (id, name, description, status, document_count, chunk_count, created_at, updated_at)
                   VALUES (%s, %s, %s, %s, %s, %s, %s, %s)""",
                ("existing-kb-123", "我的知识库", "用户创建的", "ready", 5, 100, now, now)
            )
            
            # 模拟 ChromaDB 中存在同名 KB
            mock_col = MockChromaCollection("kb_existing-kb-123", count=50)
            mock_client = MockChromaClient([mock_col])
            
            with patch('watcher_ai.services.kb_service.chromadb.PersistentClient', return_value=mock_client), \
                 patch('os.path.exists', return_value=True):
                
                _sync_from_chroma()
                
                # 验证名称没有被覆盖
                result = client.query_one("SELECT name FROM knowledge_bases WHERE id = %s", ("existing-kb-123",))
                assert result is not None, "知识库应该保留"
                assert result['name'] == "我的知识库", "名称应该保持不变"
        finally:
            # 清理
            client.execute("DELETE FROM knowledge_bases WHERE id = %s", ("existing-kb-123",))
    
    def test_sync_creates_new_kb_when_not_in_mysql(self):
        """验证当 MySQL 中不存在时，创建新知识库"""
        from watcher_ai.database.mysql_client import MySQLClient
        MySQLClient._instance = None
        
        from watcher_ai.services.kb_repository import KBRepository
        from watcher_ai.services.kb_service import _sync_from_chroma
        
        client = MySQLClient()
        
        # 清理测试数据
        client.execute("DELETE FROM knowledge_bases WHERE id = %s", ("new-kb-456",))
        
        try:
            # 模拟 ChromaDB 中存在 KB，但 MySQL 中不存在
            mock_col = MockChromaCollection("kb_new-kb-456", count=30)
            mock_client = MockChromaClient([mock_col])
            
            with patch('watcher_ai.services.kb_service.chromadb.PersistentClient', return_value=mock_client), \
                 patch('os.path.exists', return_value=True):
                
                _sync_from_chroma()
                
                # 验证创建了新知识库
                result = client.query_one("SELECT * FROM knowledge_bases WHERE id = %s", ("new-kb-456",))
                assert result is not None, "应该创建新知识库"
                assert result['name'] == "KB_new-kb-4", "应该使用默认名称"
                assert result['chunk_count'] == 30, "应该同步 chunk 数量"
        finally:
            # 清理
            client.execute("DELETE FROM knowledge_bases WHERE id = %s", ("new-kb-456",))
