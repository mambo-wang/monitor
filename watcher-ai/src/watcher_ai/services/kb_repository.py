"""知识库数据库操作层"""
import uuid
from datetime import datetime
from typing import List, Optional, Dict, Any

from watcher_ai.database.mysql_client import MySQLClient

class KBRepository:
    """知识库数据库操作类"""
    
    TABLE_NAME = "knowledge_bases"
    
    @staticmethod
    def _get_client() -> MySQLClient:
        """获取数据库客户端"""
        return MySQLClient()
    
    @staticmethod
    def create(name: str, description: str = "") -> Dict[str, Any]:
        """创建知识库"""
        kb_id = str(uuid.uuid4())
        now = datetime.now()
        
        sql = f"""
            INSERT INTO {KBRepository.TABLE_NAME} 
            (id, name, description, status, document_count, chunk_count, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
        """
        params = (kb_id, name, description, 'idle', 0, 0, now, now)
        
        KBRepository._get_client().execute(sql, params)
        
        return {
            'id': kb_id,
            'name': name,
            'description': description,
            'status': 'idle',
            'document_count': 0,
            'chunk_count': 0,
            'created_at': now,
            'updated_at': now
        }
    
    @staticmethod
    def get_by_id(kb_id: str) -> Optional[Dict[str, Any]]:
        """根据ID获取知识库"""
        sql = f"SELECT * FROM {KBRepository.TABLE_NAME} WHERE id = %s"
        return KBRepository._get_client().query_one(sql, (kb_id,))
    
    @staticmethod
    def list_all() -> List[Dict[str, Any]]:
        """获取所有知识库"""
        sql = f"SELECT * FROM {KBRepository.TABLE_NAME} ORDER BY updated_at DESC"
        return KBRepository._get_client().query_all(sql)
    
    @staticmethod
    def update_status(kb_id: str, status: str) -> bool:
        """更新知识库状态"""
        sql = f"""
            UPDATE {KBRepository.TABLE_NAME} 
            SET status = %s, updated_at = %s 
            WHERE id = %s
        """
        KBRepository._get_client().execute(sql, (status, datetime.now(), kb_id))
        return True
    
    @staticmethod
    def update_counts(kb_id: str, document_count: int, chunk_count: int) -> bool:
        """更新文档和块计数"""
        sql = f"""
            UPDATE {KBRepository.TABLE_NAME} 
            SET document_count = %s, chunk_count = %s, updated_at = %s 
            WHERE id = %s
        """
        KBRepository._get_client().execute(
            sql, (document_count, chunk_count, datetime.now(), kb_id)
        )
        return True
    
    @staticmethod
    def delete(kb_id: str) -> bool:
        """删除知识库"""
        sql = f"DELETE FROM {KBRepository.TABLE_NAME} WHERE id = %s"
        KBRepository._get_client().execute(sql, (kb_id,))
        return True
    
    @staticmethod
    def exists(kb_id: str) -> bool:
        """检查知识库是否存在"""
        sql = f"SELECT 1 FROM {KBRepository.TABLE_NAME} WHERE id = %s"
        result = KBRepository._get_client().query_one(sql, (kb_id,))
        return result is not None

    @staticmethod
    def update(kb_id: str, name: str = None, description: str = None) -> bool:
        """更新知识库的名称和描述"""
        if not KBRepository.exists(kb_id):
            return False

        now = datetime.now()
        updates = []
        params = []

        if name is not None:
            updates.append("name = %s")
            params.append(name)
        if description is not None:
            updates.append("description = %s")
            params.append(description)

        if not updates:
            return True

        updates.append("updated_at = %s")
        params.append(now)
        params.append(kb_id)

        sql = f"UPDATE {KBRepository.TABLE_NAME} SET {', '.join(updates)} WHERE id = %s"
        KBRepository._get_client().execute(sql, tuple(params))
        return True
    
    @staticmethod
    def save(kb_data: Dict[str, Any]) -> bool:
        """保存或更新知识库"""
        sql = f"""
            INSERT INTO {KBRepository.TABLE_NAME} 
            (id, name, description, status, document_count, chunk_count, created_at, updated_at)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s)
            ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            description = VALUES(description),
            status = VALUES(status),
            document_count = VALUES(document_count),
            chunk_count = VALUES(chunk_count),
            updated_at = VALUES(updated_at)
        """
        params = (
            kb_data['id'],
            kb_data['name'],
            kb_data.get('description', ''),
            kb_data.get('status', 'idle'),
            kb_data.get('document_count', 0),
            kb_data.get('chunk_count', 0),
            kb_data.get('created_at', datetime.now()),
            kb_data.get('updated_at', datetime.now())
        )
        KBRepository._get_client().execute(sql, params)
        return True
