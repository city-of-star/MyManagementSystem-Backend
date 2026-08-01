package com.mms.base.service.finance.service;

import com.mms.base.common.finance.dto.FinancePayrollConfigSaveDto;
import com.mms.base.common.finance.vo.FinancePayrollConfigVo;

/**
 * 实现功能【工资录入配置服务】
 *
 * @author li.hongyu
 * @date 2026-08-01
 */
public interface FinancePayrollConfigService {

    /**
     * 获取当前用户配置；若无则按账户/分类名尝试生成默认配置
     */
    FinancePayrollConfigVo getCurrent();

    /**
     * 保存当前用户配置（整表替换明细行）
     */
    FinancePayrollConfigVo save(FinancePayrollConfigSaveDto dto);
}
