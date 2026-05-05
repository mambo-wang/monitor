package com.virtual.cloud.om.agent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分布式锁 - MongoDB 版本已禁用
 * MySQL 单机版使用 MySQL 锁替代
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatcherLock {
    private String id;
    private long expireAt;
    private String token;
}
