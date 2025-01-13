package com.virtual.cloud.om.service.lock;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.virtual.cloud.om.entity.WatcherLock;
import com.virtual.cloud.om.sdk.api.LockApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @Author: w22798
 * @Date: 2022/5/21 10:17
 */
@Slf4j
@Service("lockApi")
public class WatcherLockService implements LockApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String acquire(String key, long expiration) {
        Query query = Query.query(Criteria.where("_id").is(key));
        String token = UUID.randomUUID().toString();
        Update update = new Update()
                .setOnInsert("_id", key)
                .setOnInsert("expireAt", System.currentTimeMillis() + expiration)
                .setOnInsert("token", token);

        FindAndModifyOptions options = new FindAndModifyOptions().upsert(true)
                .returnNew(true);
        WatcherLock doc = mongoTemplate.findAndModify(query, update, options,
                WatcherLock.class);
        boolean locked = doc.getToken() != null && doc.getToken().equals(token);

        // 如果已过期
        if (!locked && doc.getExpireAt() < System.currentTimeMillis()) {
            DeleteResult deleted = this.mongoTemplate.remove(
                    Query.query(Criteria.where("_id").is(key)
                            .and("token").is(doc.getToken())
                            .and("expireAt").is(doc.getExpireAt())),
                    WatcherLock.class);
            if (deleted.getDeletedCount() >= 1) {
                // 成功释放锁， 再次尝试获取锁
                return this.acquire(key, expiration);
            }
        }

        log.debug("[lock] Tried to acquire lock for key {} with token {} . Locked: {}",
                key, token, locked);
        return locked ? token : null;
    }

    @Override
    public boolean release(String key, String token) {
        if(StringUtils.isEmpty(token)){
            return true;
        }
        Query query = Query.query(Criteria.where("_id").is(key)
                .and("token").is(token));
        DeleteResult deleted = mongoTemplate.remove(query, WatcherLock.class);
        boolean released = deleted.getDeletedCount() == 1;
        if (released) {
            log.debug("[lock] Remove query successfully affected 1 record for key {} with token {}",
                    key, token);
        } else if (deleted.getDeletedCount() > 0) {
            log.error("[lock] Unexpected result from release for key {} with token {}, released {}",
                    key, token, deleted);
        } else {
            log.error("[lock] Remove query did not affect any records for key {} with token {}",
                    key, token);
        }

        return released;
    }

    @Override
    public boolean refresh(String key, String token, long expiration) {
        Query query = Query.query(Criteria.where("_id").is(key)
                .and("token").is(token));
        Update update = Update.update("expireAt",
                System.currentTimeMillis() + expiration);
        UpdateResult updated =
                mongoTemplate.updateFirst(query, update, WatcherLock.class);

        final boolean refreshed = updated.getModifiedCount() == 1;
        if (refreshed) {
            log.info("[lock] Refresh query successfully affected 1 record for key {} " +
                    "with token {}", key, token);
        } else if (updated.getModifiedCount() > 0) {
            log.error("[lock] Unexpected result from refresh for key {} with token {}, " +
                    "released {}", key, token, updated);
        } else {
            log.warn("[lock] Refresh query did not affect any records for key {} with token {}. " +
                            "This is possible when refresh interval fires for the final time " +
                            "after the lock has been released",
                    key, token);
        }

        return refreshed;
    }
}
