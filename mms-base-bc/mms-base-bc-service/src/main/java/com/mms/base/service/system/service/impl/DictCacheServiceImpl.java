package com.mms.base.service.system.service.impl;

import com.mms.base.common.system.entity.DictTypeEntity;
import com.mms.base.service.system.mapper.DictTypeMapper;
import com.mms.base.service.system.service.DictCacheService;
import com.mms.common.cache.constants.CacheNameConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 实现功能【字典缓存服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-17 15:27:06
 */
@Slf4j
@Service
public class DictCacheServiceImpl implements DictCacheService {

    /**
     * 通过代理对象触发 Spring Cache AOP（避免类内自调用导致 @CacheEvict 不生效）
     */
    @Resource
    @Lazy
    private DictCacheService dictCacheServiceProxy;

    @Resource
    private DictTypeMapper dictTypeMapper;

    @Override
    @CacheEvict(cacheNames = CacheNameConstants.Base.DICT_DATA, key = "#dictTypeCode")
    public void clearDictCacheByCode(String dictTypeCode) {}

    @Override
    public void clearDictCacheById(Long dictTypeId) {
        try {
            if (dictTypeId == null) {
                return;
            }
            DictTypeEntity dictType = dictTypeMapper.selectById(dictTypeId);
            if (dictType == null) {
                return;
            }
            dictCacheServiceProxy.clearDictCacheByCode(dictType.getDictTypeCode());
            log.info("已清除字典类型 {}（{}）的字典数据缓存", dictType.getDictTypeCode(), dictTypeId);
        } catch (Exception e) {
            // 缓存清除失败不影响主流程，只记录日志
            log.error("清除字典类型 {} 的字典数据缓存失败：{}", dictTypeId, e.getMessage(), e);
        }
    }
}