"""KBRepository单元测试"""
import pytest
import sys
import os
from datetime import datetime

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

class TestKBRepository:
    """KBRepository测试类"""
    
    def setup_method(self):
        """每个测试前清理测试数据"""
        from watcher_ai.database.mysql_client import MySQLClient
        from watcher_ai.services.kb_repository import KBRepository
        
        # 重置单例
        MySQLClient._instance = None
        client = MySQLClient()
        
        # 清理测试数据
        client.execute("DELETE FROM knowledge_bases WHERE name LIKE %s", ("测试%",))
    
    def teardown_method(self):
        """每个测试后清理测试数据"""
        from watcher_ai.database.mysql_client import MySQLClient
        MySQLClient._instance = None
        client = MySQLClient()
        client.execute("DELETE FROM knowledge_bases WHERE name LIKE %s", ("测试%",))
    
    def test_create(self):
        """验证 KBRepository.create() 创建知识库"""
        from watcher_ai.services.kb_repository import KBRepository
        
        kb = KBRepository.create("测试知识库", "这是一个测试")
        
        assert kb['id'] is not None
        assert kb['name'] == "测试知识库"
        assert kb['description'] == "这是一个测试"
        assert kb['status'] == 'idle'
        assert kb['document_count'] == 0
        assert kb['chunk_count'] == 0
    
    def test_get_by_id(self):
        """验证 KBRepository.get_by_id() 按ID查询"""
        from watcher_ai.services.kb_repository import KBRepository
        
        # 先创建
        created = KBRepository.create("测试KB", "")
        # 再查询
        result = KBRepository.get_by_id(created['id'])
        
        assert result is not None
        assert result['id'] == created['id']
        assert result['name'] == "测试KB"
    
    def test_list_all(self):
        """验证 KBRepository.list_all() 查询所有"""
        from watcher_ai.services.kb_repository import KBRepository
        
        KBRepository.create("测试KB1", "")
        KBRepository.create("测试KB2", "")
        
        result = KBRepository.list_all()
        
        assert len(result) >= 2
        names = [kb['name'] for kb in result]
        assert "测试KB1" in names
        assert "测试KB2" in names
    
    def test_update_status(self):
        """验证 KBRepository.update_status() 更新状态"""
        from watcher_ai.services.kb_repository import KBRepository
        
        created = KBRepository.create("测试KB", "")
        KBRepository.update_status(created['id'], "building")
        
        result = KBRepository.get_by_id(created['id'])
        assert result['status'] == "building"
    
    def test_update_counts(self):
        """验证 KBRepository.update_counts() 更新计数"""
        from watcher_ai.services.kb_repository import KBRepository
        
        created = KBRepository.create("测试KB", "")
        KBRepository.update_counts(created['id'], 5, 100)
        
        result = KBRepository.get_by_id(created['id'])
        assert result['document_count'] == 5
        assert result['chunk_count'] == 100
    
    def test_delete(self):
        """验证 KBRepository.delete() 删除知识库"""
        from watcher_ai.services.kb_repository import KBRepository
        
        created = KBRepository.create("测试KB", "")
        result = KBRepository.delete(created['id'])
        
        assert result is True
        assert KBRepository.get_by_id(created['id']) is None
    
    def test_exists(self):
        """验证 KBRepository.exists() 检查存在"""
        from watcher_ai.services.kb_repository import KBRepository
        
        created = KBRepository.create("测试KB", "")
        
        assert KBRepository.exists(created['id']) is True
        assert KBRepository.exists("non-existent-id") is False
    
    def test_save_update(self):
        """验证 KBRepository.save() 更新已有记录"""
        from watcher_ai.services.kb_repository import KBRepository
        
        created = KBRepository.create("测试KB", "")
        
        # 使用save更新
        created['name'] = "更新后的名称"
        created['status'] = "ready"
        KBRepository.save(created)
        
        result = KBRepository.get_by_id(created['id'])
        assert result['name'] == "更新后的名称"
        assert result['status'] == "ready"
