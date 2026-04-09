package com.mms.usercenter.common.websocket.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【Websocket Token】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-09 14:31:41
 */
@Data
@Schema(description = "Websocket Token")
public class WebsocketTokenVo {

    @Schema(description = "Websocket 握手令牌", requiredMode = Schema.RequiredMode.REQUIRED, example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String websocketToken;

    @Schema(description = "Websocket 握手令牌过期时间（秒）", requiredMode = Schema.RequiredMode.REQUIRED, example = "900")
    private Long websocketTokenExpiresIn;
}