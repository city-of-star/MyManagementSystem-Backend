package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.material.MaterialRequests;
import com.mms.intern.common.api.material.MaterialResponses;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.service.InternMaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class InternMaterialController {

    private final InternMaterialService materialService;

    @PostMapping
    public Response<Void> submit(@RequestBody @Valid MaterialRequests.Submit dto) {
        materialService.submit(dto);
        return Response.success();
    }

    @PutMapping("/{id}")
    public Response<Void> update(@PathVariable Long id, @RequestBody @Valid MaterialRequests.Submit dto) {
        materialService.update(id, dto);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        materialService.delete(id);
        return Response.success();
    }

    @GetMapping("/list")
    public Response<List<MaterialResponses.ListItem>> list(@RequestParam Long applicationId) {
        return Response.success(materialService.list(applicationId));
    }

    @PostMapping("/page")
    public Response<PageResultVo<MaterialResponses.ListItem>> page(@RequestBody MaterialRequests.PageQuery q) {
        return Response.success(materialService.page(q));
    }

    @GetMapping("/{id}")
    public Response<MaterialResponses.Detail> get(@PathVariable Long id) {
        return Response.success(materialService.get(id));
    }

    @PutMapping("/{id}/audit")
    public Response<Void> audit(@PathVariable Long id, @RequestBody @Valid MaterialRequests.Audit dto) {
        materialService.audit(id, dto);
        return Response.success();
    }
}
