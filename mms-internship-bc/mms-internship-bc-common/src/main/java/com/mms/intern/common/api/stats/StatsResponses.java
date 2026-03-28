package com.mms.intern.common.api.stats;

import lombok.Data;

@Data
public class StatsResponses {

    @Data
    public static class BatchStatistics {
        private Long batchId;
        private String batchName;
        private Integer enterpriseCount;
        private Integer positionCount;
        private Integer applicationTotal;
        private Integer applicationPending;
        private Integer applicationApproved;
        private Integer applicationInProgress;
        private Integer applicationCompleted;
        private Integer weeklyLogPendingReview;
    }

    @Data
    public static class Overview {
        private Integer batchCount;
        private Integer activeBatchCount;
        private BatchStatistics currentBatchStats;
    }
}
