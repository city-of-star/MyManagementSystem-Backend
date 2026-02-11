package com.mms.common.threadpool.enums;

/**
 * 实现功能【拒绝策略枚举】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-11 17:09:20
 */
public enum RejectedPolicy {
    /**
     * 由调用方线程执行任务
     */
    CALLER_RUNS,
    /**
     * 直接抛出 RejectedExecutionException
     */
    ABORT,
    /**
     * 直接丢弃任务，不抛异常
     */
    DISCARD,
    /**
     * 丢弃队列中最旧的任务，然后尝试提交当前任务
     */
    DISCARD_OLDEST
}