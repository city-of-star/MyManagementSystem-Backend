package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.stats.StatsResponses;
import com.mms.intern.core.service.InternStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stats")
@RequiredArgsConstructor
public class InternStatsController {

    private final InternStatsService statsService;

    @GetMapping("/batch/{batchId}")
    public Response<StatsResponses.BatchStatistics> batch(@PathVariable Long batchId) {
        return Response.success(statsService.batchStats(batchId));
    }

    @GetMapping("/overview")
    public Response<StatsResponses.Overview> overview(@RequestParam(required = false) Long batchId) {
        return Response.success(statsService.overview(batchId));
    }
}
