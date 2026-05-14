-- 对话历史表迁移脚本
-- 执行前请确保数据库已创建

-- 创建会话表
CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(36) PRIMARY KEY COMMENT '会话ID (UUID)',
    user_id VARCHAR(64) NOT NULL COMMENT '用户标识',
    kb_id VARCHAR(36) NOT NULL COMMENT '关联知识库ID',
    title VARCHAR(255) DEFAULT '' COMMENT '会话标题（首条消息前20字）',
    message_count INT DEFAULT 0 COMMENT '消息总数',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    updated_at DATETIME NOT NULL COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_kb_id (kb_id),
    INDEX idx_updated_at (updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';

-- 创建消息表
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(36) NOT NULL COMMENT '所属会话ID',
    role VARCHAR(16) NOT NULL COMMENT '角色: user/assistant',
    content TEXT NOT NULL COMMENT '消息内容',
    sources JSON COMMENT '来源文档（assistant消息）',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    INDEX idx_session_id (session_id),
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话消息表';
