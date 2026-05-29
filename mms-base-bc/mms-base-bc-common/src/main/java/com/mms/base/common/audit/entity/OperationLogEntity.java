package com.mms.base.common.audit.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseIdEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 实现功能【用户操作日志实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 10:00:00
 */
@Data
@TableName("audit_operation_log")
@Schema(description = "用户操作日志实体")
public class OperationLogEntity extends BaseIdEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "链路追踪ID")
    private String traceId;

    @Schema(description = "操作用户ID")
    private Long userId;

    @Schema(description = "操作用户名")
    private String username;

    @Schema(description = "业务模块")
    private String module;

    @Schema(description = "操作类型：create/update/delete/export/assign/login/logout等")
    private String operationType;

    @Schema(description = "操作描述")
    private String operationDesc;

    @Schema(description = "请求方法：GET/POST/PUT/DELETE")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求IP")
    private String requestIp;

    @Schema(description = "请求参数（脱敏后）")
    private String requestParams;

    @Schema(description = "响应结果摘要")
    private String responseData;

    @Schema(description = "操作状态：0-失败，1-成功")
    private Integer operationStatus;

    @Schema(description = "失败原因/异常摘要")
    private String errorMessage;

    @Schema(description = "耗时（毫秒）")
    private Long costMs;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;
}
