package com.mms.base.service.system.service;

/**
 * 实现功能【字典缓存服务】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-17 15:26:27
 */
public interface DictCacheService {

    /**
     * 根据字典类型编码清除缓存
     *
     * @param dictTypeCode 字典类型编码
     */
    void clearDictCacheByCode(String dictTypeCode);

    /**
     * 根据字典类型ID清除缓存
     *
     * @param dictTypeId 字典类型ID
     */
    void clearDictCacheById(Long dictTypeId);
}

