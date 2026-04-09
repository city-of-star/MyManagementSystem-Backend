package com.mms.usercenter.service.websocket.service;

import com.mms.usercenter.common.websocket.vo.WebsocketTokenVo;

/**
 * 实现功能【Websocket 服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2026-04-09 14:27:41
 */
public interface WebsocketService {

    /**
     * 获取 Websocket Token
     *
     * @return Websocket Token
     */
    WebsocketTokenVo getWsHandshakeToken();
}