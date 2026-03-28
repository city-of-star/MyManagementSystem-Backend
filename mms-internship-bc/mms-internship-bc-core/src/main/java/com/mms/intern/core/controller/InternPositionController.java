package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.position.PositionRequests;
import com.mms.intern.common.api.position.PositionResponses;
import com.mms.intern.common.vo.PageResultVo;
import com.mms.intern.core.service.InternPositionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
public class InternPositionController {

    private final InternPositionService positionService;

    @PostMapping("/page")
    public Response<PageResultVo<PositionResponses.ListItem>> page(@RequestBody PositionRequests.PageQuery q) {
        return Response.success(positionService.page(q));
    }

    @GetMapping("/open")
    public Response<PageResultVo<PositionResponses.OpenItem>> open(@ModelAttribute @Valid PositionRequests.OpenQuery q) {
        return Response.success(positionService.openPage(q));
    }

    @GetMapping("/{id}")
    public Response<PositionResponses.Detail> get(@PathVariable Long id) {
        return Response.success(positionService.get(id));
    }

    @PostMapping
    public Response<Void> save(@RequestBody @Valid PositionRequests.Save dto) {
        positionService.save(dto);
        return Response.success();
    }

    @PutMapping("/{id}")
    public Response<Void> update(@PathVariable Long id, @RequestBody @Valid PositionRequests.Save dto) {
        positionService.update(id, dto);
        return Response.success();
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        positionService.delete(id);
        return Response.success();
    }

    @PutMapping("/{id}/status")
    public Response<Void> status(@PathVariable Long id, @RequestBody @Valid PositionRequests.StatusBody dto) {
        positionService.updateStatus(id, dto);
        return Response.success();
    }
}
