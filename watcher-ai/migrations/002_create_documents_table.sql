-- watcher-ai/migrations/002_create_documents_table.sql
-- 文档元数据表迁移脚本

CREATE TABLE IF NOT EXISTS documents (
    id VARCHAR(36) PRIMARY KEY COMMENT '文档唯一标识符(UUID)',
    kb_id VARCHAR(36) NOT NULL COMMENT '所属知识库ID',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/parsed/error',
    chunk_count INT DEFAULT 0 COMMENT '分块数量',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_kb_id (kb_id),
    INDEX idx_status (status),
    FOREIGN KEY (kb_id) REFERENCES knowledge_bases(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档元数据表';