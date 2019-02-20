package org.cnbi.web.aop;

import java.util.UUID;

/**
 * 生成令牌和从缓存中删除令牌等管理
 */
public class TokenManagement {

    /**
     * 生成Token
     *
     * @return
     */
    public static String generateToken() {
        String token = UUID.randomUUID().toString();
        GuavaTimeCache.put(token, 1);
        return token;
    }

    /**
     * 打开表单分发Token
     *
     * @param token
     *
     * @return  Token {UUID}
     */
    public static Object getToken(String token) {
        return GuavaTimeCache.get(token);
    }

    /**
     * 使Token失效
     *
     * @param token
     */
    public static void invalidateToken(String token) {
        GuavaTimeCache.invalidate(token);
    }

}
