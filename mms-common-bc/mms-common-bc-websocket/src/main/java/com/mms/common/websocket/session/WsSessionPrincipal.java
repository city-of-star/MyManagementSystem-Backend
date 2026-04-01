package com.mms.common.websocket.session;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【WebSocket 会话主体信息】
 * <p>
 * 保存连接与业务用户之间的映射关系
 * </p>
 *
 * @author li.hongyu
 * @date 2026-03-26 16:28:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WsSessionPrincipal {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private String userId;
}

