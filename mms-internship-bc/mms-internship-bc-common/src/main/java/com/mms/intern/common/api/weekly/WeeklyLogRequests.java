package com.mms.intern.common.api.weekly;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class WeeklyLogRequests {

    @Data
    public static class Save {
        /** 新建时必填 */
        private Long applicationId;
        @NotNull
        private Integer weekIndex;
        private String title;
        @NotBlank
        private String content;
        private List<Long> attachmentIds;
    }

    @Data
    public static class MyPageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        @NotNull
        private Long applicationId;
    }

    @Data
    public static class PendingPageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private Long batchId;
        private Long applicationId;
    }

    @Data
    public static class AdminPageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private Long batchId;
        private Long applicationId;
        private String status;
        private String studentKeyword;
    }

    @Data
    public static class Review {
        @NotBlank
        private String status;
        private String reviewComment;
    }
}
