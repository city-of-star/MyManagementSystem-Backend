package com.mms.intern.common.api.enterprise;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EnterpriseRequests {

    @Data
    public static class PageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private String keyword;
        private String auditStatus;
        private Integer status;
    }

    @Data
    public static class Save {
        @NotBlank
        private String enterpriseName;
        private String creditCode;
        private String contactName;
        private String contactPhone;
        private String address;
        private String intro;
        private String remark;
    }

    @Data
    public static class Audit {
        @NotBlank
        private String auditStatus;
        private String auditRemark;
    }
}
