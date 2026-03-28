package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.application.ApplicationRequests;
import com.mms.intern.common.api.application.ApplicationResponses;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.service.InternApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/application")
@RequiredArgsConstructor
public class InternApplicationController {

    private final InternApplicationService applicationService;

    @PostMapping
    public Response<Void> apply(@RequestBody @Valid ApplicationRequests.Apply dto) {
        applicationService.apply(dto);
        return Response.success();
    }

    @PostMapping("/assign")
    public Response<Void> assign(@RequestBody @Valid ApplicationRequests.Assign dto) {
        applicationService.assign(dto);
        return Response.success();
    }

    @PutMapping("/{id}/cancel")
    public Response<Void> cancel(@PathVariable Long id) {
        applicationService.cancel(id);
        return Response.success();
    }

    @GetMapping("/my/page")
    public Response<PageResultVo<ApplicationResponses.ListItem>> myPage(@ModelAttribute ApplicationRequests.MyPageQuery q) {
        return Response.success(applicationService.myPage(q));
    }

    @PostMapping("/page")
    public Response<PageResultVo<ApplicationResponses.ListItem>> page(@RequestBody ApplicationRequests.AdminPageQuery q) {
        return Response.success(applicationService.adminPage(q));
    }

    @GetMapping("/{id}")
    public Response<ApplicationResponses.Detail> get(@PathVariable Long id) {
        return Response.success(applicationService.get(id));
    }

    @PutMapping("/{id}/audit")
    public Response<Void> audit(@PathVariable Long id, @RequestBody @Valid ApplicationRequests.Audit dto) {
        applicationService.audit(id, dto);
        return Response.success();
    }

    @PutMapping("/{id}/mentor")
    public Response<Void> mentor(@PathVariable Long id, @RequestBody @Valid ApplicationRequests.Mentor dto) {
        applicationService.setMentor(id, dto);
        return Response.success();
    }

    @PutMapping("/{id}/enterprise-mentor")
    public Response<Void> enterpriseMentor(@PathVariable Long id, @RequestBody ApplicationRequests.EnterpriseMentor dto) {
        applicationService.setEnterpriseMentor(id, dto);
        return Response.success();
    }

    @PutMapping("/{id}/lifecycle")
    public Response<Void> lifecycle(@PathVariable Long id, @RequestBody @Valid ApplicationRequests.Lifecycle dto) {
        applicationService.lifecycle(id, dto);
        return Response.success();
    }
}
