package com.mms.intern.common.api.batch;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchRequests {

    @Data
    public static class PageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private String keyword;
        private Integer active;
    }

    @Data
    public static class Save {
        @NotBlank
        private String batchName;
        private String schoolYear;
        private String term;
        private LocalDateTime signUpStart;
        private LocalDateTime signUpEnd;
        private Integer active = 1;
        private String remark;
    }
}
