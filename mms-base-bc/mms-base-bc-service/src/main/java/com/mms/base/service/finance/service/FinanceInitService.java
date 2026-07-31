package com.mms.base.service.finance.service;

/**
 * 实现功能【记账用户初始化服务】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
public interface FinanceInitService {

    /**
     * 若当前用户未初始化，则从全局模板拷贝账户/分类/快捷项
     */
    void ensureInitialized();

    /**
     * 当前用户是否已从全局模板初始化
     */
    boolean isInitialized();
}
