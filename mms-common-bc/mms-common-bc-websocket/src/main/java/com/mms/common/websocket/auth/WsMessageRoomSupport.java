package com.mms.common.websocket.auth;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.lang.Nullable;

/**
 * 实现功能【从 WebSocket 消息 data 中解析房间 ID】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-04-15 10:15:26
 */
public final class WsMessageRoomSupport {

    private WsMessageRoomSupport() {
        throw new UnsupportedOperationException();
    }

    /**
     * 支持 data 为纯文本 roomId，或 JSON 对象 {"roomId":"..."}
     */
    @Nullable
    public static String extractRoomId(@Nullable JsonNode dataNode) {
        if (dataNode == null || dataNode.isMissingNode() || dataNode.isNull()) {
            return null;
        }
        if (dataNode.isTextual()) {
            String roomId = dataNode.asText();
            return roomId.isBlank() ? null : roomId;
        }
        JsonNode roomIdNode = dataNode.path("roomId");
        if (roomIdNode.isTextual()) {
            String roomId = roomIdNode.asText();
            return roomId.isBlank() ? null : roomId;
        }
        return null;
    }
}

