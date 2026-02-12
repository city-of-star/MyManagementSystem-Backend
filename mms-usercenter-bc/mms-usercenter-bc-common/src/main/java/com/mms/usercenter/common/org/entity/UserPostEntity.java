package com.mms.usercenter.common.org.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mms.common.datasource.entity.BaseCreateEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;

/**
 * 实现功能【用户岗位关联实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 14:42:42
 */
@Data
@TableName("user_post")
@Schema(description = "用户岗位关联实体")
public class UserPostEntity extends BaseCreateEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "岗位ID")
    private Long postId;

    @Schema(description = "是否主岗位：0-否，1-是")
    private Integer isPrimary;
}

