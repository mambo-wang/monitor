"""MySQL客户端测试"""
import pytest
import sys
import os

sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

def test_mysql_client_singleton():
    """验证 MySQLClient 是单例模式"""
    from watcher_ai.database.mysql_client import MySQLClient
    
    # 重置单例以确保测试独立性
    MySQLClient._instance = None
    
    client1 = MySQLClient()
    client2 = MySQLClient()
    assert client1 is client2, "MySQLClient 应该是单例"

def test_mysql_client_has_execute_method():
    """验证 MySQLClient 有 execute 方法"""
    from watcher_ai.database.mysql_client import MySQLClient
    
    MySQLClient._instance = None
    client = MySQLClient()
    assert hasattr(client, 'execute'), "MySQLClient 应该有 execute 方法"
    assert hasattr(client, 'query_one'), "MySQLClient 应该有 query_one 方法"
    assert hasattr(client, 'query_all'), "MySQLClient 应该有 query_all 方法"
    assert hasattr(client, 'execute_many'), "MySQLClient 应该有 execute_many 方法"

def test_mysql_client_connection():
    """验证 MySQLClient 可以连接数据库"""
    from watcher_ai.database.mysql_client import MySQLClient
    
    MySQLClient._instance = None
    client = MySQLClient()
    
    # 执行简单查询验证连接
    result = client.query_one("SELECT 1 as test")
    assert result is not None
    assert result['test'] == 1

def test_mysql_client_crud():
    """验证 MySQLClient CRUD 操作"""
    from watcher_ai.database.mysql_client import MySQLClient
    from datetime import datetime
    
    MySQLClient._instance = None
    client = MySQLClient()
    
    test_id = "test-mysql-client-001"
    test_name = "测试MySQL客户端"
    
    try:
        # INSERT
        now = datetime.now()
        client.execute(
            """INSERT INTO knowledge_bases 
               (id, name, description, status, document_count, chunk_count, created_at, updated_at)
               VALUES (%s, %s, %s, %s, %s, %s, %s, %s)""",
            (test_id, test_name, "测试描述", "idle", 0, 0, now, now)
        )
        
        # SELECT ONE
        result = client.query_one("SELECT * FROM knowledge_bases WHERE id = %s", (test_id,))
        assert result is not None
        assert result['name'] == test_name
        
        # UPDATE
        client.execute("UPDATE knowledge_bases SET status = %s WHERE id = %s", ("building", test_id))
        result = client.query_one("SELECT status FROM knowledge_bases WHERE id = %s", (test_id,))
        assert result['status'] == "building"
        
        # SELECT ALL
        results = client.query_all("SELECT * FROM knowledge_bases")
        assert len(results) > 0
        
        # DELETE
        client.execute("DELETE FROM knowledge_bases WHERE id = %s", (test_id,))
        result = client.query_one("SELECT * FROM knowledge_bases WHERE id = %s", (test_id,))
        assert result is None
        
    finally:
        # 清理测试数据
        client.execute("DELETE FROM knowledge_bases WHERE id = %s", (test_id,))
