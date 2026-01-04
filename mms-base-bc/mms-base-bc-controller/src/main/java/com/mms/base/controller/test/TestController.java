package com.mms.base.controller.test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.test.dto.TestDTO;
import com.mms.base.common.test.entity.TestEntity;
import com.mms.base.service.test.service.TestService;
import com.mms.common.core.response.Response;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 实现功能【测试基础功能】
 *
 * @author li.hongyu
 * @date 2025-10-12 19:33:28
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/1")
    public Response<Map<String, Object>> test1() {
        return Response.success(testService.test());
    }

    @PostMapping("/getPage")
    public Response<Page<TestEntity>> getPage(@RequestBody TestDTO dto) {
        return Response.success(testService.getPage(dto));
    }
}