package com.virtual.cloud.om.sdk.api;

public interface LockApi {

    /**
     * 获取锁
     * @param key key
     * @param expiration 锁有效期
     * @return token，如果获取锁
     */
    String acquire(String key, long expiration);

    /**
     * 释放锁
     * @param key key
     * @param token token
     * @return 结果
     */
    boolean release(String key, String token);

    /**
     * 刷新锁，修改锁失效时间
     * @param key key
     * @param token token
     * @param expiration 有效期
     * @return 结果
     */
    boolean refresh(String key, String token, long expiration);
}
