package org.cnbi.web.aop;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.TimeUnit;

/**
 * Guava限时缓存 管理
 */
public class GuavaTimeCache {

    // 1小时缓存，同Session销毁时间
    public static Cache<Object, Object> CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public static Object get(Object key) {
        return CACHE.getIfPresent(key);
    }

    public static void put(Object key, Object value) {
        CACHE.put(key, value);
    }

    public static void invalidate(Object key) {
        CACHE.invalidate(key);
    }



}
