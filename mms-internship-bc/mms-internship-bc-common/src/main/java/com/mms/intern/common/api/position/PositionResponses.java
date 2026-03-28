package com.mms.intern.common.api.position;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PositionResponses {

    @Data
    public static class ListItem {
        private Long id;
        private Long batchId;
        private String batchName;
        private Long enterpriseId;
        private String enterpriseName;
        private String title;
        private Integer quota;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer appliedCount;
        private LocalDateTime createTime;
    }

    @Data
    public static class OpenItem {
        private Long id;
        private String enterpriseName;
        private String title;
        private Integer quota;
        private Integer appliedCount;
        private String requirement;
        private LocalDate startDate;
        private LocalDate endDate;
    }

    @Data
    public static class Detail extends ListItem {
        private String requirement;
        private String remark;
        private LocalDateTime updateTime;
    }
}
