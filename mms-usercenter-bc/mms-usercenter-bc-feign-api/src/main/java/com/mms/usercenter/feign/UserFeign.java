package com.mms.usercenter.feign;

import com.mms.usercenter.common.auth.vo.UserDetailVo;
import com.mms.common.core.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 实现功能【用户管理Feign客户端】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2025-01-04 17:21:08
 */
@FeignClient(name = "usercenter", path = "/user")
public interface UserFeign {

    /**
     * 根据用户名获取用户详细信息
     */
    @GetMapping("/username/{username}")
    Response<UserDetailVo> getUserByUsername(@PathVariable("username") String username);

    /**
     * 根据用户ID获取用户详细信息
     */
    @GetMapping("/{userId}")
    Response<UserDetailVo> getUserById(@PathVariable("userId") Long userId);
}

