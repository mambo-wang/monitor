package com.virtual.cloud.om.agent.service.lock;

import com.virtual.cloud.om.sdk.api.LockApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁服务 - MySQL 单机版使用内存锁
 * 当 MongoDB 禁用时使用此实现
 * @Author: w22798
 * @Date: 2022/5/21 10:17
 */
@Slf4j
@Service("lockApi")
public class WatcherLockService implements LockApi {

    private final Map<String, LockInfo> locks = new ConcurrentHashMap<>();

    private static class LockInfo {
        String token;
        long expireAt;
        
        LockInfo(String token, long expireAt) {
            this.token = token;
            this.expireAt = expireAt;
        }
    }

    @Override
    public String acquire(String key, long expiration) {
        long now = System.currentTimeMillis();
        LockInfo existingLock = locks.get(key);
        
        if (existingLock != null && existingLock.expireAt > now) {
            return null;
        }
        
        String token = UUID.randomUUID().toString();
        locks.put(key, new LockInfo(token, now + expiration));
        log.debug("[lock] Acquired lock for key {} with token {}", key, token);
        return token;
    }

    @Override
    public boolean release(String key, String token) {
        LockInfo existingLock = locks.get(key);
        if (existingLock != null && existingLock.token.equals(token)) {
            locks.remove(key);
            log.debug("[lock] Released lock for key {} with token {}", key, token);
            return true;
        }
        return false;
    }

    @Override
    public boolean refresh(String key, String token, long expiration) {
        LockInfo existingLock = locks.get(key);
        if (existingLock != null && existingLock.token.equals(token)) {
            existingLock.expireAt = System.currentTimeMillis() + expiration;
            log.debug("[lock] Refreshed lock for key {} with token {}", key, token);
            return true;
        }
        return false;
    }
}
