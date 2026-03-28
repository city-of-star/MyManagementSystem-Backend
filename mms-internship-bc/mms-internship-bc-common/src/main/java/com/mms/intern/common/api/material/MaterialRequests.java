package com.mms.intern.common.api.material;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialRequests {

    @Data
    public static class Submit {
        @NotNull
        private Long applicationId;
        @NotBlank
        private String materialType;
        private String materialName;
        @NotNull
        private Long attachmentId;
        private String remark;
    }

    @Data
    public static class PageQuery {
        private int pageNum = 1;
        private int pageSize = 10;
        private Long batchId;
        private Long applicationId;
        private String materialType;
        private String status;
    }

    @Data
    public static class Audit {
        @NotBlank
        private String status;
        private String auditRemark;
    }
}
