package com.mms.common.mq.rocket.config;

import com.mms.common.mq.api.service.MqSendService;
import com.mms.common.mq.rocket.service.impl.NoOpMqSendService;
import com.mms.common.mq.rocket.service.impl.RocketMqSendService;
import com.mms.common.mq.rocket.support.MqMessageSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import com.mms.common.mq.rocket.properties.MmsMqProperties;

/**
 * 实现功能【RocketMQ 模块自动装配】
 * <p>
 * 只要 Spring 已创建 {@link RocketMQTemplate}（即已配置 rocketmq.name-server），就注册真实发送实现，
 * 不再要求 mms.mq.enabled=true。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 16:30:00
 */
@Slf4j
@AutoConfiguration
@AutoConfigureAfter(name = "org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration")
@EnableConfigurationProperties(MmsMqProperties.class)
public class RocketMqAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MqMessageSerializer mqMessageSerializer() {
        return new MqMessageSerializer();
    }

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    @ConditionalOnMissingBean(MqSendService.class)
    public MqSendService rocketMqSendService(RocketMQTemplate rocketMQTemplate,
                                             MqMessageSerializer mqMessageSerializer) {
        return new RocketMqSendService(rocketMQTemplate, mqMessageSerializer);
    }

    @Bean
    @ConditionalOnMissingBean(MqSendService.class)
    public MqSendService noOpMqSendService() {
        return new NoOpMqSendService();
    }

    @Bean
    @ConditionalOnBean(MqSendService.class)
    ApplicationRunner mqSendServiceStartupLogger(MqSendService mqSendService, Environment environment) {
        return args -> {
            log.info("MqSendService 实现：{}，mms.mq.enabled={}",
                    mqSendService.getClass().getSimpleName(),
                    environment.getProperty("mms.mq.enabled", "未配置"));
            if (mqSendService instanceof NoOpMqSendService) {
                log.warn("当前为 NoOp：请配置 rocketmq.name-server 并确认 RocketMQTemplate 已创建；"
                        + "若 Nacos 中 mms.mq.enabled=false 不影响发送（仅影响消费者开关）");
            }
        };
    }
}
