package com.mms.common.mq.rocket.config;

import com.mms.common.core.config.CoreAutoConfiguration;
import com.mms.common.core.utils.JacksonObjectMapperUtils;
import com.mms.common.mq.api.service.MqSendService;
import com.mms.common.mq.rocket.properties.MmsMqProperties;
import com.mms.common.mq.rocket.service.impl.NoOpMqSendService;
import com.mms.common.mq.rocket.service.impl.RocketMqSendService;
import com.mms.common.mq.rocket.support.MqMessageSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 实现功能【RocketMQ 模块自动装配】
 * <p>
 * 1. {@code mms.mq.enabled=true} 且存在 {@link RocketMQTemplate} 时注册真实发送实现
 * 2. 否则注册 {@link NoOpMqSendService}，便于本地无 Broker 开发
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@AutoConfiguration
@AutoConfigureAfter({CoreAutoConfiguration.class, RocketMQAutoConfiguration.class})
@EnableConfigurationProperties(MmsMqProperties.class)
public class RocketMqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(name = JacksonObjectMapperUtils.COMMON_OBJECT_MAPPER_BEAN_NAME)
    public MqMessageSerializer mqMessageSerializer(@Qualifier(JacksonObjectMapperUtils.COMMON_OBJECT_MAPPER_BEAN_NAME) ObjectMapper objectMapper) {
        return new MqMessageSerializer(objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "mms.mq", name = "enabled", havingValue = "true")
    @ConditionalOnBean({RocketMQTemplate.class, MqMessageSerializer.class})
    @ConditionalOnMissingBean(MqSendService.class)
    public MqSendService rocketMqSendService(RocketMQTemplate rocketMQTemplate, MqMessageSerializer mqMessageSerializer) {
        return new RocketMqSendService(rocketMQTemplate, mqMessageSerializer);
    }

    @Bean
    @ConditionalOnMissingBean(MqSendService.class)
    public MqSendService noOpMqSendService() {
        return new NoOpMqSendService();
    }
}
