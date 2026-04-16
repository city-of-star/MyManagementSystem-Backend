package com.mms.usercenter.common.security.constants;

/**
 * 实现功能【】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-15 11:04:42
 */
public class OnlineUserConstants {

    /**
     * 在线用户房间标识
     */
    public static final String ROOM_ONLINE_USER = "security_online_user";

    /**
     * 在线用户全量快照消息
     */
    public static final String TYPE_ONLINE_USER_FULL = "online_user_full";

    /**
     * 在线用户新增/更新消息
     */
    public static final String TYPE_ONLINE_USER_UPSERT = "online_user_upsert";

    /**
     * 在线用户移除消息
     */
    public static final String TYPE_ONLINE_USER_REMOVE = "online_user_remove";

    /**
     * 私有构造函数，防止实例化
     */
    private OnlineUserConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}