package com.mms.base.feign.usercenter;

import com.mms.base.feign.usercenter.dto.UserAuthorityDto;
import com.mms.common.core.response.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usercenter", path = "/usercenter/authority")
public interface UserAuthorityFeign {

    @GetMapping("/{username}")
    Response<UserAuthorityDto> getUserAuthorities(@PathVariable("username") String username);
}

