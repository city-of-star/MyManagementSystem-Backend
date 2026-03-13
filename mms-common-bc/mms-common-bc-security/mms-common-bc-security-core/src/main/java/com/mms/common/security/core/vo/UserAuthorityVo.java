package com.mms.common.security.core.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 实现功能【用户角色/权限返回对象】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 20:31:11
 */
@Data
public class UserAuthorityVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 角色编码集合
     */
    private Set<String> roles = new HashSet<>();

    /**
     * 权限编码集合
     */
    private Set<String> permissions = new HashSet<>();
}

