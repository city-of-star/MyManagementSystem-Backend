package com.mms.intern.common.api.weekly;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WeeklyLogResponses {

    @Data
    public static class ListItem {
        private Long id;
        private Long applicationId;
        private Integer weekIndex;
        private String title;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime reviewTime;
    }

    @Data
    public static class Detail extends ListItem {
        private String content;
        private List<Long> attachmentIds;
        private String reviewComment;
        private Long reviewBy;
        private LocalDateTime updateTime;
    }
}
