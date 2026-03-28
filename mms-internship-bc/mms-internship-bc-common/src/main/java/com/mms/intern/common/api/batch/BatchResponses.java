package com.mms.intern.common.api.batch;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BatchResponses {

    @Data
    public static class ListItem {
        private Long id;
        private String batchName;
        private String schoolYear;
        private String term;
        private LocalDateTime signUpStart;
        private LocalDateTime signUpEnd;
        private Integer active;
        private LocalDateTime createTime;
    }

    @Data
    public static class Detail extends ListItem {
        private String remark;
        private Long createBy;
        private Long updateBy;
        private LocalDateTime updateTime;
    }

    @Data
    public static class Option {
        private Long id;
        private String batchName;
        private String schoolYear;
        private String term;
    }
}
