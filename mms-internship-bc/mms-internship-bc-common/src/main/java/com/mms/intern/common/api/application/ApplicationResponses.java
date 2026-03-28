package com.mms.intern.common.api.application;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponses {

    @Data
    public static class ListItem {
        private Long id;
        private Long batchId;
        private String batchName;
        private Long positionId;
        private String positionTitle;
        private String enterpriseName;
        private Long studentUserId;
        private String studentName;
        private String studentNo;
        private Long schoolMentorUserId;
        private String schoolMentorName;
        private Long enterpriseMentorUserId;
        private String status;
        private LocalDateTime createTime;
        private LocalDateTime auditTime;
    }

    @Data
    public static class Detail extends ListItem {
        private String auditRemark;
        private Long auditBy;
        private String remark;
        private String requirement;
        private LocalDateTime updateTime;
    }
}
