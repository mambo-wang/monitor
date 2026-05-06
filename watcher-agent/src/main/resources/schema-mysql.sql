-- ================================================
-- ShowTime MySQL 单机版数据库初始化脚本
-- ================================================

CREATE DATABASE IF NOT EXISTS watcher_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE watcher_db;

-- ================================================
-- 1. 用户表 (SysUser)
-- ================================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码(加密)',
    last_login_time DATETIME COMMENT '最近登录时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 初始化默认管理员用户 (用户名: admin, 密码: Admin@123 - SM4加密)
INSERT INTO sys_user (id, username, password) VALUES
('1', 'admin', 'iesB4yJHVdE1R3mP4yT6LA==');

-- ================================================
-- 2. 密码策略表 (PwdStrategy)
-- ================================================
DROP TABLE IF EXISTS pwd_strategy;
CREATE TABLE pwd_strategy (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    min_length INT DEFAULT 8 COMMENT '密码最小长度',
    pwd_complex INT DEFAULT 1 COMMENT '密码复杂度: 1-简单 2-中等 3-复杂 4-最强',
    pwd_life_time INT DEFAULT 90 COMMENT '密码有效期(天)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='密码策略表';

INSERT INTO pwd_strategy (id, min_length, pwd_complex, pwd_life_time) VALUES
('1', 8, 1, 90);

-- ================================================
-- 3. Agent唯一码表 (AgentUniqueCode)
-- ================================================
DROP TABLE IF EXISTS agent_unique_code;
CREATE TABLE agent_unique_code (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    uid VARCHAR(128) NOT NULL UNIQUE COMMENT 'Agent唯一标识',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent唯一码表';

-- ================================================
-- 4. 部署记录表 (Deploy)
-- ================================================
DROP TABLE IF EXISTS deploy;
CREATE TABLE deploy (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(255) COMMENT '部署名称',
    type VARCHAR(50) COMMENT '部署类型',
    status VARCHAR(50) COMMENT '部署状态',
    host VARCHAR(255) COMMENT '主机地址',
    config TEXT COMMENT '配置信息(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部署记录表';

-- ================================================
-- 5. 参数配置表 (Parameter)
-- ================================================
DROP TABLE IF EXISTS parameter;
CREATE TABLE parameter (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    type VARCHAR(100) NOT NULL COMMENT '参数类型',
    name VARCHAR(255) NOT NULL COMMENT '参数名称',
    value TEXT COMMENT '参数值',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_type_name (type, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- ================================================
-- 6. 任务表 (Task)
-- ================================================
DROP TABLE IF EXISTS task;
CREATE TABLE task (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(255) NOT NULL COMMENT '任务名称',
    type VARCHAR(50) COMMENT '任务类型',
    status VARCHAR(50) COMMENT '任务状态',
    cron_expr VARCHAR(100) COMMENT 'CRON表达式',
    handler_class VARCHAR(255) COMMENT '处理器类名',
    params TEXT COMMENT '任务参数(JSON)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- ================================================
-- 7. 操作日志表 (OperationLog) - 简化版
-- ================================================
DROP TABLE IF EXISTS operation_log;
CREATE TABLE operation_log (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    module VARCHAR(100) COMMENT '操作模块',
    operation VARCHAR(255) COMMENT '操作描述',
    operator VARCHAR(100) COMMENT '操作人',
    result VARCHAR(50) COMMENT '操作结果',
    time VARCHAR(50) COMMENT '操作时间',
    deleted VARCHAR(1) DEFAULT 'n' COMMENT '是否删除: y/n',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 添加索引
CREATE INDEX idx_operation_log_deleted ON operation_log(deleted);
CREATE INDEX idx_operation_log_time ON operation_log(time);
CREATE INDEX idx_parameter_type_name ON parameter(type, name);
CREATE INDEX idx_deploy_status ON deploy(status);
CREATE INDEX idx_task_status ON task(status);

-- ================================================
-- 8. 数据中心配置表 (DataCenterConfig)
-- ================================================
DROP TABLE IF EXISTS data_center_config;
CREATE TABLE data_center_config (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    config_key VARCHAR(100) NOT NULL UNIQUE COMMENT '配置键',
    config_value TEXT COMMENT '配置值',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据中心配置表';

-- ================================================
-- 9. 告警记录表 (Warn)
-- ================================================
DROP TABLE IF EXISTS warn;
CREATE TABLE warn (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    level VARCHAR(20) COMMENT '告警级别',
    title VARCHAR(255) COMMENT '告警标题',
    content TEXT COMMENT '告警内容',
    source VARCHAR(100) COMMENT '告警来源',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态: pending/resolved',
    warn_time DATETIME COMMENT '告警时间',
    resolve_time DATETIME COMMENT '解决时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

CREATE INDEX idx_warn_status ON warn(status);
CREATE INDEX idx_warn_level ON warn(level);

-- ================================================
-- 10. 资源表 (Resource)
-- ================================================
DROP TABLE IF EXISTS resource;
CREATE TABLE resource (
    id VARCHAR(64) PRIMARY KEY COMMENT '资源ID',
    resource_name VARCHAR(255) COMMENT '资源名称',
    platform VARCHAR(50) COMMENT '平台类型: workspace/uis/cas/onestor',
    ip_address VARCHAR(100) NOT NULL COMMENT 'IP地址',
    port INT COMMENT '端口',
    protocol VARCHAR(20) DEFAULT 'HTTP' COMMENT '协议: HTTP/HTTPS',
    auth_type VARCHAR(50) DEFAULT 'Digest' COMMENT '认证类型',
    ac VARCHAR(100) COMMENT 'REST认证用户名',
    ci VARCHAR(255) COMMENT 'REST认证密码(加密)',
    server_username VARCHAR(100) COMMENT '管理节点用户名',
    server_password VARCHAR(255) COMMENT '管理节点密码(加密)',
    server_port INT DEFAULT 22 COMMENT '管理节点端口',
    active INT DEFAULT 1 COMMENT '是否激活: 0-否 1-是',
    usable INT DEFAULT 1 COMMENT '是否可用: 0-不可用 1-可用',
    remote INT DEFAULT 0 COMMENT 'SSH权限: 0-禁用 1-启用',
    end_time DATETIME COMMENT 'SSH关闭时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_ip_platform (ip_address, platform)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';

CREATE INDEX idx_resource_platform ON resource(platform);
CREATE INDEX idx_resource_ip ON resource(ip_address);
CREATE INDEX idx_resource_usable ON resource(usable);

-- ================================================
-- 11. 指标数据表 (MetricData)
-- ================================================
DROP TABLE IF EXISTS metric_data;
CREATE TABLE metric_data (
    id VARCHAR(64) PRIMARY KEY COMMENT '主键ID',
    resource_id VARCHAR(64) NOT NULL COMMENT '资源ID',
    resource_ip VARCHAR(100) COMMENT '资源IP地址',
    platform VARCHAR(50) COMMENT '平台类型',
    metric_type VARCHAR(100) NOT NULL COMMENT '指标类型',
    metric_name VARCHAR(255) NOT NULL COMMENT '指标名称',
    metric_value VARCHAR(500) COMMENT '指标值',
    metric_unit VARCHAR(50) COMMENT '指标单位',
    tags VARCHAR(500) COMMENT '标签(JSON)',
    report_time DATETIME COMMENT '数据上报时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='指标数据表';

CREATE INDEX idx_metric_resource_id ON metric_data(resource_id);
CREATE INDEX idx_metric_type ON metric_data(metric_type);
CREATE INDEX idx_metric_report_time ON metric_data(report_time);
CREATE INDEX idx_metric_platform ON metric_data(platform);
