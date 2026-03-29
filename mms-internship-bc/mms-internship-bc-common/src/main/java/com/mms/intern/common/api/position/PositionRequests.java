package com.mms.intern.common.api.position;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PositionRequests {

    @Data
    public static class PageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private Long batchId;
        private Long enterpriseId;
        private String keyword;
        private String status;
    }

    @Data
    public static class OpenQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        /** 为空时查询全部批次下已发布岗位 */
        private Long batchId;
        private String keyword;
    }

    @Data
    public static class Save {
        @NotNull
        private Long batchId;
        @NotNull
        private Long enterpriseId;
        @NotBlank
        private String title;
        @NotNull
        private Integer quota;
        private String requirement;
        private LocalDate startDate;
        private LocalDate endDate;
        private String remark;
    }

    @Data
    public static class StatusBody {
        @NotBlank
        private String status;
    }
}
