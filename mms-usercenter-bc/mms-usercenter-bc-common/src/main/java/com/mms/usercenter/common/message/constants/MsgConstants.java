package com.mms.usercenter.common.message.constants;

/**
 * 实现功能【消息系统常量】
 * <p>
 * 公告范围、状态、业务类型、WS 推送与列表排序等公共常量
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-03 23:10:00
 */
public final class MsgConstants {

    // ---------- 公告发送范围 scopeType ----------

    /** 指定用户（须传 userIds） */
    public static final int SCOPE_USER = 1;

    /** 指定角色（须传 roleIds，展开为角色下启用用户） */
    public static final int SCOPE_ROLE = 2;

    /** 全员（所有启用状态用户） */
    public static final int SCOPE_ALL = 3;

    // ---------- 公告发送状态 status ----------

    /** 待发送：已落库，尚未（或即将）开始扇出 */
    public static final int ANNOUNCE_PENDING = 0;

    /** 发送中：扇出进行中 */
    public static final int ANNOUNCE_RUNNING = 1;

    /** 已完成：目标用户均已投递成功 */
    public static final int ANNOUNCE_DONE = 2;

    /** 失败：存在投递失败（或部分失败） */
    public static final int ANNOUNCE_FAILED = 3;

    /** 已撤回：收件箱不再展示，公告管理仍可见 */
    public static final int ANNOUNCE_RECALLED = 4;

    // ---------- 业务类型 / WS ----------

    /** 收件箱业务类型：系统公告 */
    public static final String BIZ_TYPE_ANNOUNCE = "ANNOUNCE";

    /** 收件箱业务类型：记账快捷模板到期提醒 */
    public static final String BIZ_TYPE_FINANCE_RECURRING_DUE = "FINANCE_RECURRING_DUE";

    /** WebSocket 推送类型：未读数变更 */
    public static final String WS_TYPE_MSG_UNREAD = "msg_unread";

    // ---------- 扇出 / 列表 ----------

    /** 公告扇出每批用户数 */
    public static final int FANOUT_BATCH_SIZE = 200;

    /** 列表排序：未读优先（默认） */
    public static final String SORT_UNREAD = "unread";

    /** 列表排序：纯时间倒序（铃铛最近消息） */
    public static final String SORT_TIME = "time";

    private MsgConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
