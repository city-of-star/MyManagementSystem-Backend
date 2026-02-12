package com.mms.usercenter.common.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseCreateEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【用户部门关联实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 14:42:31
 */
@Data
@TableName("user_dept")
@Schema(description = "用户部门关联实体")
public class UserDeptEntity extends BaseCreateEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "是否主部门：0-否，1-是")
    private Integer isPrimary;
}


