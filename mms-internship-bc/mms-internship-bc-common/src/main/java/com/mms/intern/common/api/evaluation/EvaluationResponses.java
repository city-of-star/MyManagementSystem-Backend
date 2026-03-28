package com.mms.intern.common.api.evaluation;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EvaluationResponses {

    @Data
    public static class Vo {
        private Long id;
        private Long applicationId;
        private BigDecimal schoolScore;
        private String schoolComment;
        private Long schoolBy;
        private LocalDateTime schoolTime;
        private BigDecimal enterpriseScore;
        private String enterpriseComment;
        private Long enterpriseBy;
        private LocalDateTime enterpriseTime;
        private BigDecimal finalScore;
        private String finalRemark;
        private LocalDateTime updateTime;
    }
}
