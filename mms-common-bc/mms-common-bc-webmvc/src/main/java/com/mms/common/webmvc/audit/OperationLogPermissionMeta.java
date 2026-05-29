package com.mms.common.webmvc.audit;

/**
 * 实现功能【操作日志权限映射元数据】
 * <p>
 * 表示一条权限码对应的审计展示信息：业务模块、操作类型、操作描述。
 * 由 {@link com.mms.common.webmvc.audit.OperationLogPermissionMappings} 按权限码查表返回。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-29 14:00:00
 */
public record OperationLogPermissionMeta(String module, String operationType, String operationDesc) {
}
