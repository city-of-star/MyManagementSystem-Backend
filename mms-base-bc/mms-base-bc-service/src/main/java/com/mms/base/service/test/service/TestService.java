package com.mms.base.service.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.test.dto.TestDTO;
import com.mms.base.common.test.entity.TestEntity;

import java.util.Map;

/**
 * 实现功能【测试服务】
 *
 * @author li.hongyu
 * @date 2025-10-12 20:02:55
 */
public interface TestService {

    Map<String, Object> test();

    Page<TestEntity> getPage(TestDTO dto);
}