package com.mms.common.webmvc.audit;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实现功能【操作日志 MQ 载荷】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
@Data
public class OperationLogRecordMqPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String traceId;

    private Long userId;

    private String username;

    private String module;

    private String operationType;

    private String operationDesc;

    private String requestMethod;

    private String requestUrl;

    private String requestIp;

    private String requestParams;

    private String responseData;

    private Integer operationStatus;

    private String errorMessage;

    private Long costMs;

    private LocalDateTime operationTime;
}
