package com.mms.usercenter.common.security.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实现功能【】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-03-05 17:50:10
 */
@Data
public class SecurityUserDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password;
    private String realName;
    private Integer status;
    private Integer locked;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
}