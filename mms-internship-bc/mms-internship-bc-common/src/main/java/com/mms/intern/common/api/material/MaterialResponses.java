package com.mms.intern.common.api.material;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MaterialResponses {

    @Data
    public static class ListItem {
        private Long id;
        private Long applicationId;
        private String materialType;
        private String materialName;
        private Long attachmentId;
        private String fileName;
        private String status;
        private String auditRemark;
        private LocalDateTime createTime;
    }

    @Data
    public static class Detail extends ListItem {
        private Long auditBy;
        private LocalDateTime auditTime;
        private LocalDateTime updateTime;
    }
}
