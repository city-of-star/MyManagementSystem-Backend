package com.mms.usercenter.common.security.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户角色/权限返回对象
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

