package com.mms.base.controller.finance;

import com.mms.base.common.finance.vo.FinanceInitStatusVo;
import com.mms.base.service.finance.service.FinanceInitService;
import com.mms.common.core.response.Response;
import com.mms.common.security.servlet.annotations.RequiresPermission;
import com.mms.common.security.servlet.constants.PermissionConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【记账用户初始化 Controller】
 *
 * @author li.hongyu
 * @date 2026-07-31
 */
@Tag(name = "记账初始化", description = "用户首次进入记账时从全局模板拷贝骨架")
@RestController
@RequestMapping("/finance/init")
public class FinanceInitController {

    @Resource
    private FinanceInitService financeInitService;

    @Operation(summary = "查询当前用户是否已初始化记账")
    @RequiresPermission(PermissionConstants.FINANCE)
    @GetMapping("/status")
    public Response<FinanceInitStatusVo> status() {
        FinanceInitStatusVo vo = new FinanceInitStatusVo();
        vo.setInitialized(financeInitService.isInitialized());
        return Response.success(vo);
    }

    @Operation(summary = "确保已初始化（未初始化则从全局模板拷贝）")
    @RequiresPermission(PermissionConstants.FINANCE)
    @PostMapping("/ensure")
    public Response<Void> ensure() {
        financeInitService.ensureInitialized();
        return Response.success();
    }
}
