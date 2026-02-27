package com.mms.common.job;

/**
 * 实现功能【定时任务处理器接口】
 * <p>
 * 所有的定时任务处理器都需要实现该接口
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-25 11:06:28
 */
public interface JobHandler {

    /**
     * 执行定时任务
     *
     * @param paramsJson 任务参数 JSON 字符串（允许为空）
     */
    void execute(String paramsJson);

}