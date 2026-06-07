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
     * 是否启用 RocketMQ（发送与消费者）；false 时发送走 NoOp，{@code @RocketMQMessageListener} 监听器不注册
     */
    private boolean enabled = true;
}
