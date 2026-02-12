package com.mms.usercenter.common.auth.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【权限实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 14:42:17
 */
@Data
@TableName("permission")
@Schema(description = "权限实体")
public class PermissionEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "父权限ID，0表示顶级权限")
    private Long parentId;

    @Schema(description = "权限类型：catalog-目录，menu-菜单，button-按钮，api-接口")
    private String permissionType;

    @Schema(description = "权限名称")
    private String permissionName;

    @Schema(description = "权限编码（唯一标识）")
    private String permissionCode;

    @Schema(description = "路由路径（菜单类型使用）")
    private String path;

    @Schema(description = "组件路径（菜单类型使用）")
    private String component;

    @Schema(description = "图标（菜单类型使用）")
    private String icon;

    @Schema(description = "接口URL（接口类型使用）")
    private String apiUrl;

    @Schema(description = "接口请求方式：GET,POST,PUT,DELETE等（接口类型使用）")
    private String apiMethod;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否显示：0-隐藏，1-显示")
    private Integer visible;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;
}