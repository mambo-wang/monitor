package com.virtual.cloud.om.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * mongodb 分布式锁
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "WatcherLock")
public class WatcherLock {
    @Id
    private String id;
    private long expireAt;
    private String token;
}
