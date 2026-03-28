package com.mms.intern.core.controller;

import com.mms.common.core.response.Response;
import com.mms.intern.common.api.evaluation.EvaluationRequests;
import com.mms.intern.common.api.evaluation.EvaluationResponses;
import com.mms.intern.core.service.InternEvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluation")
@RequiredArgsConstructor
public class InternEvaluationController {

    private final InternEvaluationService evaluationService;

    @GetMapping("/by-application/{applicationId}")
    public Response<EvaluationResponses.Vo> byApplication(@PathVariable Long applicationId) {
        return Response.success(evaluationService.getByApplication(applicationId));
    }

    @GetMapping("/{id}")
    public Response<EvaluationResponses.Vo> get(@PathVariable Long id) {
        return Response.success(evaluationService.get(id));
    }

    @PutMapping("/school")
    public Response<Void> school(@RequestBody @Valid EvaluationRequests.School dto) {
        evaluationService.saveSchool(dto);
        return Response.success();
    }

    @PutMapping("/enterprise")
    public Response<Void> enterprise(@RequestBody @Valid EvaluationRequests.Enterprise dto) {
        evaluationService.saveEnterprise(dto);
        return Response.success();
    }

    @PostMapping("/{applicationId}/finalize")
    public Response<Void> finalize(@PathVariable Long applicationId,
                                   @RequestBody(required = false) EvaluationRequests.Finalize dto) {
        evaluationService.finalizeScore(applicationId, dto == null ? new EvaluationRequests.Finalize() : dto);
        return Response.success();
    }
}
