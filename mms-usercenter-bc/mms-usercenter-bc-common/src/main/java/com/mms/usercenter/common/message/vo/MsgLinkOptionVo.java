package com.mms.usercenter.common.message.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【公告跳转页面选项（菜单树）】
 * <p>
 * catalog 节点 disabled；menu 且有 path 的节点 value=path，可选。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-08-06
 */
@Data
@Schema(description = "公告跳转页面选项")
public class MsgLinkOptionVo {

    @Schema(description = "节点值：菜单为路由 path，目录为 c:{id}")
    private String value;

    @Schema(description = "展示名称")
    private String label;

    @Schema(description = "菜单图标名（Element Plus Icons）")
    private String icon;

    @Schema(description = "是否禁用（目录不可选）")
    private Boolean disabled;

    @Schema(description = "子节点")
    private List<MsgLinkOptionVo> children = new ArrayList<>();
}
