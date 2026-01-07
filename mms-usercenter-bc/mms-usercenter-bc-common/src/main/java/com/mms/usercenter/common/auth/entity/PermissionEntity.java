package com.mms.usercenter.common.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
public class PermissionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "权限ID")
    private Long id;

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

    @TableLogic(value = "0", delval = "1")
    @Schema(description = "逻辑删除标记：0-未删除，1-已删除")
    private Integer deleted;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新人ID")
    private Long updateBy;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}