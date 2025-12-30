package com.mms.usercenter.feign.test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "base", path = "/test")
public interface BaseTestFeign {

    @GetMapping("/1")
    String test1();
}


