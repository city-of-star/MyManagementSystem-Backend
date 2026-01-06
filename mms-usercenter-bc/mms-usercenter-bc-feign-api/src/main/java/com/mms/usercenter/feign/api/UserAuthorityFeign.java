package com.mms.usercenter.feign.api;

import com.mms.usercenter.feign.api.vo.UserAuthorityVo;
import com.mms.common.core.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 实现功能【用户权限Feign客户端】
 * <p>
 * 用于调用用户中心服务的用户权限查询接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-01-04 17:21:08
 */
@FeignClient(name = "usercenter", path = "/authority")
public interface UserAuthorityFeign {

    /**
     * 根据用户名查询角色与权限（带缓存）
     */
    @GetMapping("/{username}")
    Response<UserAuthorityVo> getUserAuthorities(@PathVariable("username") String username);
}

