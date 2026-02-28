package com.mms.base.service.system.job.dto;

import lombok.Data;

import java.util.List;

/**
 * 实现功能【附件清理任务参数DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-02-26 10:01:26
 */
@Data
public class AttachmentCleanJobDto {

    /**
     * 批次大小（每次处理多少条记录）
     */
    private Integer batchSize = 100;

    /**
     * 延迟删除天数（逻辑删除后多少天才物理删除）
     */
    private Integer deletedDays = 30;

    /**
     * 是否删除物理文件，false时仅删除数据库记录
     */
    private Boolean deletePhysicalFile = true;

    /**
     * 存储类型过滤，可选值：local、oss，null表示不限制
     */
    private String storageType;

    /**
     * 业务类型过滤，null表示不限制
     */
    private String businessType;

    /**
     * 文件类型过滤，传数组如 ["jpg", "png"]，null表示不限制
     */
    private List<String> fileType;

    /**
     * 最大文件大小（字节），只删除小于等于此大小的文件，null表示不限制
     */
    private Long maxFileSize;

    /**
     * 最小文件大小（字节），只删除大于等于此大小的文件，null表示不限制
     */
    private Long minFileSize;

    /**
     * 路径匹配模式（正则表达式），null表示不限制
     */
    private String pathPattern;

    /**
     * 重试次数（删除失败时的重试次数）
     */
    private Integer retryCount = 0;

    /**
     * 遇到错误时是否继续处理其他记录
     */
    private Boolean continueOnError = true;

    /**
     * 排序方式，可选值：id、updateTime、createTime、fileSize
     */
    private String orderBy = "id";
}
