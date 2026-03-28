package com.mms.intern.common.api.evaluation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class EvaluationRequests {

    @Data
    public static class School {
        @NotNull
        private Long applicationId;
        private BigDecimal schoolScore;
        private String schoolComment;
    }

    @Data
    public static class Enterprise {
        @NotNull
        private Long applicationId;
        private BigDecimal enterpriseScore;
        private String enterpriseComment;
    }

    @Data
    public static class Finalize {
        private BigDecimal schoolWeight;
        private BigDecimal enterpriseWeight;
        private String finalRemark;
    }
}
