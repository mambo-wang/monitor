"""KBService单元测试"""
import pytest
import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

class TestKBService:
    """KBService测试类"""
    
    def setup_method(self):
        """每个测试前清理测试数据"""
        from watcher_ai.database.mysql_client import MySQLClient
        from watcher_ai.services.kb_service import KBService
        
        # 重置单例
        MySQLClient._instance = None
        
        client = MySQLClient()
        client.execute("DELETE FROM knowledge_bases WHERE name LIKE %s", ("测试%",))
    
    def teardown_method(self):
        """每个测试后清理测试数据"""
        from watcher_ai.database.mysql_client import MySQLClient
        MySQLClient._instance = None
        client = MySQLClient()
        client.execute("DELETE FROM knowledge_bases WHERE name LIKE %s", ("测试%",))
    
    def test_create(self):
        """验证 KBService.create() 创建知识库"""
        from watcher_ai.services.kb_service import KBService
        
        kb = KBService.create("测试知识库", "描述")
        
        assert kb['name'] == "测试知识库"
        assert kb['description'] == "描述"
        assert kb['status'] == 'idle'
    
    def test_list_all(self):
        """验证 KBService.list_all() 使用 KBRepository"""
        from watcher_ai.services.kb_service import KBService
        
        KBService.create("测试KB1", "")
        KBService.create("测试KB2", "")
        
        result = KBService.list_all()
        assert len(result) >= 2
        names = [kb['name'] for kb in result]
        assert "测试KB1" in names
        assert "测试KB2" in names
    
    def test_get_by_id(self):
        """验证 KBService.get_by_id() 从数据库查询"""
        from watcher_ai.services.kb_service import KBService
        
        created = KBService.create("测试KB", "")
        result = KBService.get_by_id(created['id'])
        
        assert result is not None
        assert result['id'] == created['id']
    
    def test_delete(self):
        """验证 KBService.delete() 从数据库删除"""
        from watcher_ai.services.kb_service import KBService
        
        created = KBService.create("测试KB", "")
        result = KBService.delete(created['id'])
        
        assert result is True
        assert KBService.get_by_id(created['id']) is None
    
    def test_update_status(self):
        """验证 KBService.update_status() 更新状态"""
        from watcher_ai.services.kb_service import KBService
        
        created = KBService.create("测试KB", "")
        KBService.update_status(created['id'], "building")
        
        result = KBService.get_by_id(created['id'])
        assert result['status'] == "building"
    
    def test_update_counts(self):
        """验证 KBService.update_counts() 更新计数"""
        from watcher_ai.services.kb_service import KBService
        
        created = KBService.create("测试KB", "")
        KBService.update_counts(created['id'], 5, 100)
        
        result = KBService.get_by_id(created['id'])
        assert result['document_count'] == 5
        assert result['chunk_count'] == 100
    
    def test_full_lifecycle(self):
        """验证完整知识库生命周期"""
        from watcher_ai.services.kb_service import KBService
        
        # 1. 创建
        kb = KBService.create("生命周期测试", "完整测试")
        kb_id = kb['id']
        
        # 2. 查询
        fetched = KBService.get_by_id(kb_id)
        assert fetched['name'] == "生命周期测试"
        
        # 3. 更新状态
        KBService.update_status(kb_id, "building")
        assert KBService.get_by_id(kb_id)['status'] == "building"
        
        # 4. 更新计数
        KBService.update_counts(kb_id, 3, 50)
        result = KBService.get_by_id(kb_id)
        assert result['document_count'] == 3
        assert result['chunk_count'] == 50
        
        # 5. 删除
        KBService.delete(kb_id)
        assert KBService.get_by_id(kb_id) is None

    def test_update_name_only(self):
        """验证 KBService.update() 只更新名称"""
        from watcher_ai.services.kb_service import KBService
        
        kb = KBService.create("旧名称", "旧描述")
        KBService.update(kb['id'], name="新名称")
        
        result = KBService.get_by_id(kb['id'])
        assert result['name'] == "新名称"
        assert result['description'] == "旧描述"
    
    def test_update_description_only(self):
        """验证 KBService.update() 只更新描述"""
        from watcher_ai.services.kb_service import KBService
        
        kb = KBService.create("名称", "旧描述")
        KBService.update(kb['id'], description="新描述")
        
        result = KBService.get_by_id(kb['id'])
        assert result['name'] == "名称"
        assert result['description'] == "新描述"
    
    def test_update_both(self):
        """验证 KBService.update() 同时更新名称和描述"""
        from watcher_ai.services.kb_service import KBService
        
        kb = KBService.create("旧名称", "旧描述")
        KBService.update(kb['id'], name="新名称", description="新描述")
        
        result = KBService.get_by_id(kb['id'])
        assert result['name'] == "新名称"
        assert result['description'] == "新描述"
    
    def test_update_nonexistent(self):
        """验证 KBService.update() 更新不存在的 KB"""
        from watcher_ai.services.kb_service import KBService
        
        result = KBService.update("nonexistent-id", name="新名称")
        assert result is False
