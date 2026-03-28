package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.batch.BatchRequests;
import com.mms.intern.common.api.batch.BatchResponses;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.service.InternBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class InternBatchController {

    private final InternBatchService batchService;

    @PostMapping("/page")
    public Response<PageResultVo<BatchResponses.ListItem>> page(@RequestBody BatchRequests.PageQuery q) {
        return Response.success(batchService.page(q));
    }

    @GetMapping("/options")
    public Response<List<BatchResponses.Option>> options() {
        return Response.success(batchService.options());
    }

    @GetMapping("/{id}")
    public Response<BatchResponses.Detail> get(@PathVariable Long id) {
        return Response.success(batchService.get(id));
    }

    @PostMapping
    public Response<Void> save(@RequestBody @Valid BatchRequests.Save dto) {
        batchService.save(dto);
        return Response.success();
    }

    @PutMapping("/{id}")
    public Response<Void> update(@PathVariable Long id, @RequestBody @Valid BatchRequests.Save dto) {
        batchService.update(id, dto);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        batchService.delete(id);
        return Response.success();
    }
}
