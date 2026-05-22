package com.mms.common.mq.rocket.annotation;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实现功能【MMS RocketMQ 监听器声明注解】
 * <p>
 * 对 {@link RocketMQMessageListener} 的别名封装，统一 topic / tag / consumerGroup 写法；
 * 已内置 {@link Component} 与 {@code mms.mq.enabled=true} 条件，业务监听器仅需标注本注解并继承
 * {@link com.mms.common.mq.rocket.listener.AbstractMqMessageListener}。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@ConditionalOnProperty(prefix = "mms.mq", name = "enabled", havingValue = "true")
@RocketMQMessageListener(topic = "", consumerGroup = "", selectorExpression = "*")
public @interface MmsRocketListener {

    @AliasFor(annotation = RocketMQMessageListener.class, attribute = "topic")
    String topic() default "";

    @AliasFor(annotation = RocketMQMessageListener.class, attribute = "selectorExpression")
    String tag() default "*";

    @AliasFor(annotation = RocketMQMessageListener.class, attribute = "consumerGroup")
    String consumerGroup() default "";

    @AliasFor(annotation = RocketMQMessageListener.class, attribute = "messageModel")
    MessageModel messageModel() default MessageModel.CLUSTERING;

    @AliasFor(annotation = RocketMQMessageListener.class, attribute = "consumeMode")
    ConsumeMode consumeMode() default ConsumeMode.CONCURRENTLY;
}
