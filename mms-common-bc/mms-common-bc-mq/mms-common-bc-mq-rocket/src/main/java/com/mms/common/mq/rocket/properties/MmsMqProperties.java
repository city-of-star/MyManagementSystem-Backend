package com.mms.common.mq.rocket.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【MMS 消息队列配置属性】
 * <p>
 *
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@Data
@ConfigurationProperties(prefix = "mms.mq")
public class MmsMqProperties {

    /**
     * 是否启用 RocketMQ 发送能力；false 时使用 NoOp 实现
     */
    private boolean enabled = false;
}
