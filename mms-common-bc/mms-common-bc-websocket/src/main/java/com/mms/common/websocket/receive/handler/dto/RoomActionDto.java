package com.mms.common.websocket.receive.handler.dto;

import lombok.Data;

/**
 * 实现功能【房间相关操作（加入房间、退出房间） DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-23 17:40:22
 */
@Data
public class RoomActionDto {

    /**
     * 房间 ID
     */
    private String roomId;
}

