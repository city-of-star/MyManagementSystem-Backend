package com.mms.intern.core.service;

import com.mms.intern.common.api.evaluation.EvaluationRequests;
import com.mms.intern.common.api.evaluation.EvaluationResponses;

public interface InternEvaluationService {

    EvaluationResponses.Vo getByApplication(Long applicationId);

    EvaluationResponses.Vo get(Long id);

    void saveSchool(EvaluationRequests.School dto);

    void saveEnterprise(EvaluationRequests.Enterprise dto);

    void finalizeScore(Long applicationId, EvaluationRequests.Finalize dto);
}
