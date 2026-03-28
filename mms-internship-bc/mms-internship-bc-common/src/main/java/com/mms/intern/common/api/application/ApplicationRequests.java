package com.mms.intern.common.api.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequests {

    @Data
    public static class Apply {
        @NotNull
        private Long positionId;
    }

    @Data
    public static class Assign {
        @NotNull
        private Long studentUserId;
        @NotNull
        private Long positionId;
        private Long schoolMentorUserId;
        private String remark;
    }

    @Data
    public static class MyPageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private Long batchId;
        private String status;
    }

    @Data
    public static class AdminPageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private Long batchId;
        private Long positionId;
        private String status;
        private String studentKeyword;
        private Long schoolMentorUserId;
    }

    @Data
    public static class Audit {
        @NotBlank
        private String status;
        private String auditRemark;
    }

    @Data
    public static class Mentor {
        @NotNull
        private Long schoolMentorUserId;
    }

    @Data
    public static class EnterpriseMentor {
        private Long enterpriseMentorUserId;
    }

    @Data
    public static class Lifecycle {
        @NotBlank
        private String status;
    }
}
