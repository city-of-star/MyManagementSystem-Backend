package com.mms.usercenter.feign.usercenter;

import com.mms.usercenter.feign.usercenter.vo.UserInfoVo;
import com.mms.common.core.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 实现功能【用户信息Feign客户端】
 * <p>
 * 用于调用用户中心服务的用户信息查询接口
 * </p>
 *
 * @author li.hongyu
 * @date 2025-01-04 17:21:08
 */
@FeignClient(name = "usercenter", path = "/info")
public interface UserInfoFeign {

    /**
     * 根据用户名获取用户详细信息
     */
    @GetMapping("/username/{username}")
    Response<UserInfoVo> getUserByUsername(@PathVariable("username") String username);

    /**
     * 根据用户ID获取用户详细信息
     */
    @GetMapping("/{userId}")
    Response<UserInfoVo> getUserById(@PathVariable("userId") Long userId);
}

