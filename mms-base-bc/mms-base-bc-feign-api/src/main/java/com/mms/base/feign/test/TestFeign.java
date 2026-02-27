package com.mms.base.feign.test;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 实现功能【测试Feign客户端】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-01-06 11:10:28
 */
@FeignClient(name = "base", path = "/test")
public interface TestFeign {

    @GetMapping("/1")
    String test1();
}


