"""端到端集成测试"""
import pytest
import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

class TestIntegration:
    """端到端集成测试类"""
    
    def setup_method(self):
        """每个测试前清理测试数据"""
        from watcher_ai.database.mysql_client import MySQLClient
        MySQLClient._instance = None
        
        client = MySQLClient()
        client.execute("DELETE FROM knowledge_bases WHERE name LIKE %s", ("集成测试%",))
    
    def teardown_method(self):
        """每个测试后清理测试数据"""
        from watcher_ai.database.mysql_client import MySQLClient
        MySQLClient._instance = None
        
        client = MySQLClient()
        client.execute("DELETE FROM knowledge_bases WHERE name LIKE %s", ("集成测试%",))
    
    def test_full_kb_lifecycle(self):
        """验证完整的知识库生命周期"""
        from watcher_ai.services.kb_service import KBService
        
        # 1. 创建知识库
        kb = KBService.create("集成测试知识库", "完整生命周期测试")
        assert kb['name'] == "集成测试知识库"
        kb_id = kb['id']
        
        # 2. 查询知识库
        fetched = KBService.get_by_id(kb_id)
        assert fetched is not None
        assert fetched['name'] == "集成测试知识库"
        
        # 3. 列出所有知识库
        all_kbs = KBService.list_all()
        assert len(all_kbs) >= 1
        
        # 4. 更新状态
        KBService.update_status(kb_id, "building")
        updated = KBService.get_by_id(kb_id)
        assert updated['status'] == "building"
        
        # 5. 更新计数
        KBService.update_counts(kb_id, 5, 100)
        updated = KBService.get_by_id(kb_id)
        assert updated['document_count'] == 5
        assert updated['chunk_count'] == 100
        
        # 6. 删除知识库
        result = KBService.delete(kb_id)
        assert result is True
        
        # 7. 验证删除
        deleted = KBService.get_by_id(kb_id)
        assert deleted is None
    
    def test_concurrent_kb_operations(self):
        """验证并发操作不会冲突"""
        from watcher_ai.services.kb_service import KBService
        
        # 创建多个知识库
        kb1 = KBService.create("集成测试KB1", "")
        kb2 = KBService.create("集成测试KB2", "")
        kb3 = KBService.create("集成测试KB3", "")
        
        # 验证都有唯一ID
        assert kb1['id'] != kb2['id']
        assert kb2['id'] != kb3['id']
        
        # 更新不同知识库的状态
        KBService.update_status(kb1['id'], "building")
        KBService.update_status(kb2['id'], "ready")
        
        # 验证状态独立
        assert KBService.get_by_id(kb1['id'])['status'] == "building"
        assert KBService.get_by_id(kb2['id'])['status'] == "ready"
        assert KBService.get_by_id(kb3['id'])['status'] == "idle"
    
    def test_kb_persistence_across_sessions(self):
        """验证知识库在不同会话间持久化"""
        from watcher_ai.database.mysql_client import MySQLClient
        from watcher_ai.services.kb_service import KBService
        
        # 模拟第一个会话：创建知识库
        kb = KBService.create("集成测试持久化", "跨会话测试")
        kb_id = kb['id']
        
        # 模拟第二个会话：重置单例
        MySQLClient._instance = None
        
        # 模拟第二个会话：查询知识库
        fetched = KBService.get_by_id(kb_id)
        assert fetched is not None
        assert fetched['name'] == "集成测试持久化"
        assert fetched['status'] == "idle"
