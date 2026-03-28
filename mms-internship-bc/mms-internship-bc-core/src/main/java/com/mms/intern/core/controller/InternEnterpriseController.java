package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.enterprise.EnterpriseRequests;
import com.mms.intern.common.api.enterprise.EnterpriseResponses;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.service.InternEnterpriseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/enterprise")
@RequiredArgsConstructor
public class InternEnterpriseController {

    private final InternEnterpriseService enterpriseService;

    @PostMapping("/page")
    public Response<PageResultVo<EnterpriseResponses.ListItem>> page(@RequestBody EnterpriseRequests.PageQuery q) {
        return Response.success(enterpriseService.page(q));
    }

    @GetMapping("/options")
    public Response<List<EnterpriseResponses.Option>> options(@RequestParam(required = false) String keyword) {
        return Response.success(enterpriseService.options(keyword));
    }

    @GetMapping("/{id}")
    public Response<EnterpriseResponses.Detail> get(@PathVariable Long id) {
        return Response.success(enterpriseService.get(id));
    }

    @PostMapping
    public Response<Void> save(@RequestBody @Valid EnterpriseRequests.Save dto) {
        enterpriseService.save(dto);
        return Response.success();
    }

    @PutMapping("/{id}")
    public Response<Void> update(@PathVariable Long id, @RequestBody @Valid EnterpriseRequests.Save dto) {
        enterpriseService.update(id, dto);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        enterpriseService.delete(id);
        return Response.success();
    }

    @PutMapping("/{id}/audit")
    public Response<Void> audit(@PathVariable Long id, @RequestBody @Valid EnterpriseRequests.Audit dto) {
        enterpriseService.audit(id, dto);
        return Response.success();
    }
}
