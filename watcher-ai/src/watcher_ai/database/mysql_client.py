"""MySQL 连接管理单例"""
import os
import pymysql
from typing import Any, Dict, List, Optional, Tuple

class MySQLClient:
    """MySQL 数据库客户端单例"""
    
    _instance: Optional['MySQLClient'] = None
    
    def __new__(cls) -> 'MySQLClient':
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance._initialized = False
        return cls._instance
    
    def __init__(self):
        if self._initialized:
            return
        
        self._host = os.getenv('MYSQL_HOST', 'localhost')
        self._port = int(os.getenv('MYSQL_PORT', '3306'))
        self._user = os.getenv('MYSQL_USER', 'root')
        self._password = os.getenv('MYSQL_PASSWORD', 'root')
        self._database = os.getenv('MYSQL_DATABASE', 'watcher_db')
        self._socket = os.getenv('MYSQL_SOCKET', '/tmp/mysql.sock')
        self._connection: Optional[pymysql.Connection] = None
        self._initialized = True
    
    def _get_connection(self) -> pymysql.Connection:
        """获取或创建数据库连接"""
        if self._connection is None or not self._connection.open:
            self._connection = pymysql.connect(
                host=self._host,
                port=self._port,
                user=self._user,
                password=self._password,
                database=self._database,
                unix_socket=self._socket,
                charset='utf8mb4',
                cursorclass=pymysql.cursors.DictCursor
            )
        return self._connection
    
    def execute(self, sql: str, params: Optional[Tuple] = None) -> int:
        """执行 SQL 并返回影响的行数"""
        conn = self._get_connection()
        with conn.cursor() as cursor:
            result = cursor.execute(sql, params)
            conn.commit()
            return result
    
    def query_one(self, sql: str, params: Optional[Tuple] = None) -> Optional[Dict[str, Any]]:
        """查询单条记录"""
        conn = self._get_connection()
        with conn.cursor() as cursor:
            cursor.execute(sql, params)
            return cursor.fetchone()
    
    def query_all(self, sql: str, params: Optional[Tuple] = None) -> List[Dict[str, Any]]:
        """查询所有记录"""
        conn = self._get_connection()
        with conn.cursor() as cursor:
            cursor.execute(sql, params)
            return cursor.fetchall()
    
    def execute_many(self, sql: str, params_list: List[Tuple]) -> int:
        """批量执行 SQL"""
        conn = self._get_connection()
        with conn.cursor() as cursor:
            result = cursor.executemany(sql, params_list)
            conn.commit()
            return result
    
    def close(self):
        """关闭数据库连接"""
        if self._connection and self._connection.open:
            self._connection.close()
            self._connection = None
