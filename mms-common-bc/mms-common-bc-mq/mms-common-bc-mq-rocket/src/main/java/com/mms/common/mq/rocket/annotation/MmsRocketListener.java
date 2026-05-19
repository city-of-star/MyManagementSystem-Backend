package com.mms.common.mq.rocket.annotation;

import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 实现功能【MMS RocketMQ 监听器声明注解】
 * <p>
 * 对 {@link RocketMQMessageListener} 的别名封装，统一 topic / tag / consumerGroup 写法。
 * 监听器类需同时标注 {@code @Component}，并在 {@code mms.mq.enabled=true} 时生效。
 * </p>
 *
 * @author li.hongyu
 * @date 2026-05-19 18:00:00
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
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
