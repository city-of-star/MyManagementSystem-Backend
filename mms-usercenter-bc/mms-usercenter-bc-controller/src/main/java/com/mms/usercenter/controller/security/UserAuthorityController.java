package com.mms.usercenter.controller.security;

import com.mms.common.core.response.Response;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.security.service.UserAuthorityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【用户权限服务 Controller】
 * <p>
 * 内部接口：提供用户角色/权限查询
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 20:30:21
 */
@RestController
@RequestMapping("/authority")
@RequiredArgsConstructor
@Tag(name = "内部接口-用户权限", description = "提供用户角色/权限查询")
public class UserAuthorityController {

    private final UserAuthorityService userAuthorityService;

    @Operation(summary = "根据用户名获取角色与权限")
    @GetMapping("/{username}")
    public Response<UserAuthorityVo> getUserAuthorities(@PathVariable String username) {
        return Response.success(userAuthorityService.getUserAuthorities(username));
    }
}

