-- 工具分享文件夹表
CREATE TABLE IF NOT EXISTS `tool_share_folder` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '文件夹名称',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父文件夹ID',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具分享文件夹表';

-- 工具分享文件表
CREATE TABLE IF NOT EXISTS `tool_share_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `tool_name` VARCHAR(100) NOT NULL COMMENT '工具名称',
  `tool_desc` VARCHAR(500) DEFAULT NULL COMMENT '工具作用描述',
  `file_path` VARCHAR(500) NOT NULL COMMENT '存储路径',
  `file_size` BIGINT NOT NULL COMMENT '文件大小',
  `download_count` BIGINT DEFAULT 0 COMMENT '下载量',
  `folder_id` BIGINT DEFAULT NULL COMMENT '所属文件夹ID',
  `create_user_id` BIGINT NOT NULL COMMENT '创建人ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `idx_folder_id` (`folder_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工具分享文件表';