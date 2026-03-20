package com.mms.common.security.core.constants;

/**
 * 实现功能【】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:42:26
 */
public final class JwtClaimsConstants {

    /**
     * JWT Claims中的用户名键
     */
    public static final String USERNAME = "username";

    /**
     * JWT Claims中的用户ID键
     */
    public static final String USER_ID = "userId";

    /**
     * JWT Claims中的Token类型键
     */
    public static final String TOKEN_TYPE = "tokenType";

    /**
     * JWT Claims 中的会话ID
     */
    public static final String SESSION_ID = "sid";

    /**
     * 私有构造函数，防止实例化
     */
    private JwtClaimsConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}