package com.mms.usercenter.common.message.constants;

/**
 * 实现功能【消息系统常量】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public final class MsgConstants {

    public static final int SCOPE_USER = 1;
    public static final int SCOPE_ROLE = 2;
    public static final int SCOPE_ALL = 3;

    public static final int ANNOUNCE_PENDING = 0;
    public static final int ANNOUNCE_RUNNING = 1;
    public static final int ANNOUNCE_DONE = 2;
    public static final int ANNOUNCE_FAILED = 3;
    /** 已撤回：收件箱不再展示 */
    public static final int ANNOUNCE_RECALLED = 4;

    public static final String BIZ_TYPE_ANNOUNCE = "ANNOUNCE";

    public static final String WS_TYPE_MSG_UNREAD = "msg_unread";

    public static final int FANOUT_BATCH_SIZE = 200;

    /** 列表排序：未读优先（默认） */
    public static final String SORT_UNREAD = "unread";

    /** 列表排序：纯时间倒序（铃铛最近消息） */
    public static final String SORT_TIME = "time";

    private MsgConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
