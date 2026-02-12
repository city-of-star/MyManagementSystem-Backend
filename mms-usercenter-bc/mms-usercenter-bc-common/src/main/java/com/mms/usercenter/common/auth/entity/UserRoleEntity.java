package com.mms.usercenter.common.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseCreateEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【用户角色关联实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 14:42:22
 */
@Data
@TableName("user_role")
@Schema(description = "用户角色关联实体")
public class UserRoleEntity extends BaseCreateEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "角色ID")
    private Long roleId;
}