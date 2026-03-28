package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.weekly.WeeklyLogRequests;
import com.mms.intern.common.api.weekly.WeeklyLogResponses;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.service.InternWeeklyLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weekly-log")
@RequiredArgsConstructor
public class InternWeeklyLogController {

    private final InternWeeklyLogService weeklyLogService;

    @PostMapping
    public Response<Void> save(@RequestBody @Valid WeeklyLogRequests.Save dto) {
        weeklyLogService.save(dto);
        return Response.success();
    }

    @PutMapping("/{id}")
    public Response<Void> update(@PathVariable Long id, @RequestBody @Valid WeeklyLogRequests.Save dto) {
        weeklyLogService.update(id, dto);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        weeklyLogService.delete(id);
        return Response.success();
    }

    @GetMapping("/{id}")
    public Response<WeeklyLogResponses.Detail> get(@PathVariable Long id) {
        return Response.success(weeklyLogService.get(id));
    }

    @GetMapping("/my/page")
    public Response<PageResultVo<WeeklyLogResponses.ListItem>> myPage(@ModelAttribute @Valid WeeklyLogRequests.MyPageQuery q) {
        return Response.success(weeklyLogService.myPage(q));
    }

    @GetMapping("/pending/page")
    public Response<PageResultVo<WeeklyLogResponses.ListItem>> pendingPage(@ModelAttribute WeeklyLogRequests.PendingPageQuery q) {
        return Response.success(weeklyLogService.pendingPage(q));
    }

    @PostMapping("/page")
    public Response<PageResultVo<WeeklyLogResponses.ListItem>> page(@RequestBody WeeklyLogRequests.AdminPageQuery q) {
        return Response.success(weeklyLogService.adminPage(q));
    }

    @GetMapping("/list-by-application")
    public Response<List<WeeklyLogResponses.ListItem>> listByApplication(@RequestParam Long applicationId) {
        return Response.success(weeklyLogService.listByApplication(applicationId));
    }

    @PutMapping("/{id}/review")
    public Response<Void> review(@PathVariable Long id, @RequestBody @Valid WeeklyLogRequests.Review dto) {
        weeklyLogService.review(id, dto);
        return Response.success();
    }
}
