package com.mms.intern.common.api.enterprise;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnterpriseResponses {

    @Data
    public static class ListItem {
        private Long id;
        private String enterpriseName;
        private String creditCode;
        private String contactName;
        private String contactPhone;
        private String auditStatus;
        private Integer status;
        private LocalDateTime createTime;
    }

    @Data
    public static class Detail extends ListItem {
        private String address;
        private String intro;
        private String auditRemark;
        private Long auditBy;
        private LocalDateTime auditTime;
        private String remark;
        private Long createBy;
        private LocalDateTime updateTime;
    }

    @Data
    public static class Option {
        private Long id;
        private String enterpriseName;
    }
}
