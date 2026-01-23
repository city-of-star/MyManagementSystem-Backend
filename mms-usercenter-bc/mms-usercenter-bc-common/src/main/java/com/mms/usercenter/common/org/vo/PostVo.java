package com.mms.usercenter.common.org.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【岗位信息响应 VO】
 * <p>
 * 用于返回岗位信息的响应对象
 * </p>
 *
 * @author li.hongyu
 * @date 2026-01-23 10:32:40
 */
@Data
@Schema(description = "岗位信息响应对象")
public class PostVo {

    @Schema(description = "岗位ID", example = "1")
    private Long id;

    @Schema(description = "岗位编码", example = "JAVA_DEV")
    private String postCode;

    @Schema(description = "岗位名称", example = "Java开发工程师")
    private String postName;

    @Schema(description = "岗位等级", example = "P5")
    private String postLevel;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "Java开发岗位备注")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2026-01-23 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2026-01-23 10:00:00")
    private LocalDateTime updateTime;
}
