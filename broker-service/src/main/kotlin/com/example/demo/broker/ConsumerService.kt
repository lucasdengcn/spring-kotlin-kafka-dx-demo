package com.example.demo.broker

import com.rabbitmq.client.Channel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.lang.Thread.sleep
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@Service
class ConsumerService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ConsumerService::class.java)
    }

    @RabbitListener(queues = ["vq-order-v2"], errorHandler = "rabbitConsumerListenerErrorHandler", ackMode = "MANUAL")
    @RabbitHandler
    fun consumeOrder(@Payload message : OrderMessage, channel: Channel, @Headers heads: Map<String, Object>){
        val redelivered = heads.get(AmqpHeaders.REDELIVERED) as Boolean
        logger.info("Consumed message [$redelivered] $message")
        var tagId = heads.get(AmqpHeaders.DELIVERY_TAG) as Long
        if (message.quantity > 10){
            if (redelivered){
                channel.basicReject(tagId, false);
            } else {
                channel.basicNack(tagId, false, true)
            }
        } else {
            channel.basicAck(tagId, false)
        }
    }

    @RabbitListener(queues = ["dlx-order"], errorHandler = "rabbitConsumerListenerErrorHandler", concurrency = "2")
    @RabbitHandler
    fun consumeDLX(@Payload message : OrderMessage, channel: Channel, @Headers heads: Map<String, Object>){
        val redelivered = heads.get(AmqpHeaders.REDELIVERED) as Boolean
        logger.info("header ${heads}")
        logger.info("Consumed DLX message [$redelivered] $message")
        var tagId = heads.get(AmqpHeaders.DELIVERY_TAG) as Long
        channel.basicAck(tagId, false)
    }

    @RabbitListener(queues = ["vq-delay"], errorHandler = "rabbitConsumerListenerErrorHandler", concurrency = "2")
    @RabbitHandler
    fun consumeDelay(@Payload message : OrderMessage, channel: Channel, @Headers heads: Map<String, Object>){
        val redelivered = heads.get(AmqpHeaders.REDELIVERED) as Boolean
        logger.info("header ${heads}")
        logger.info("Consumed Delay message [$redelivered] $message")
        var tagId = heads.get(AmqpHeaders.DELIVERY_TAG) as Long
        channel.basicAck(tagId, false)
    }

    @RabbitListener(queues = ["vq-order-v3"], errorHandler = "rabbitConsumerListenerErrorHandler", ackMode = "MANUAL", priority = "5", concurrency = "1")
    @RabbitHandler
    fun priority5Order(@Payload message : OrderMessage, channel: Channel, @Headers heads: Map<String, Object>){
        val lastInBatch = heads.get(AmqpHeaders.LAST_IN_BATCH)
        logger.info("Consumed message [p5][$lastInBatch] $message")
        var tagId = heads.get(AmqpHeaders.DELIVERY_TAG) as Long
        channel.basicAck(tagId, false)
        sleep(500)
    }

    @RabbitListener(queues = ["vq-order-v3"], errorHandler = "rabbitConsumerListenerErrorHandler", ackMode = "MANUAL", priority = "4", concurrency = "1")
    @RabbitHandler
    fun priority4Order(@Payload message : OrderMessage, channel: Channel, @Headers heads: Map<String, Object>){
        val lastInBatch = heads.get(AmqpHeaders.LAST_IN_BATCH)
        logger.info("Consumed message [p4][$lastInBatch] $message")
        var tagId = heads.get(AmqpHeaders.DELIVERY_TAG) as Long
        channel.basicAck(tagId, false)
        sleep(500)
    }

}

@Suppress("DEPRECATION")
@Component
class RabbitConsumerListenerErrorHandler : RabbitListenerErrorHandler {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(RabbitConsumerListenerErrorHandler::class.java)
    }

    override fun handleError(
        amqpMessage: Message?,
        message: org.springframework.messaging.Message<*>?,
        exception: ListenerExecutionFailedException?,
    ): Any {
        return "0";
    }


    override fun handleError(
        amqpMessage: Message?,
        channel: Channel,
        message: org.springframework.messaging.Message<*>?,
        exception: ListenerExecutionFailedException?,
    ): Any {
        logger.error("Error: $amqpMessage, $message", exception)
        return "0";
    }

}