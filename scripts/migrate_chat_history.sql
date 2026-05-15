-- 对话历史表迁移脚本
-- 用于存储用户与知识库的问答历史记录

-- 创建 chat_sessions 表（对话会话）
CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(36) PRIMARY KEY COMMENT '会话ID，UUID格式',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    kb_id VARCHAR(36) NOT NULL COMMENT '知识库ID',
    title VARCHAR(255) DEFAULT '' COMMENT '会话标题',
    message_count INT DEFAULT 0 COMMENT '消息数量',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_user_id (user_id) COMMENT '按用户查询',
    INDEX idx_updated_at (updated_at) COMMENT '按更新时间排序'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话会话表';

-- 创建 chat_messages 表（对话消息）
CREATE TABLE IF NOT EXISTS chat_messages (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(36) NOT NULL COMMENT '所属会话ID',
    role VARCHAR(16) NOT NULL COMMENT '角色：user/assistant',
    content TEXT COMMENT '消息内容',
    sources JSON COMMENT 'RAG 来源文档',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE COMMENT '级联删除',
    INDEX idx_session_id (session_id) COMMENT '按会话查询'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话消息表';
