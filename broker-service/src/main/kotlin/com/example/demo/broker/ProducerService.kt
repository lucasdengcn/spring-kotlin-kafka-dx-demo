package com.example.demo.broker

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

public const val ROUTING_KEY_ORDER_V2 = "rk-order-v2"

public const val ROUTING_KEY_ORDER_V3 = "rk-order-v3"

public const val ROUTING_KEY_ORDER_V4 = "rk-order-v4"

private const val ROUTING_KEY_ORDER_NOT_FOUND = "rk-order-non"

@Service
class ProducerService (private val rabbitTemplate: RabbitTemplate) {

    companion object {
        const val EX_DEV_DIRECT = "ex-dev-direct"
        val logger: Logger = LoggerFactory.getLogger(ProducerService::class.java)
    }

    fun sendDirectOrder(message: OrderMessage, correlationData: CorrelationData){
        rabbitTemplate.convertAndSend(EX_DEV_DIRECT, ROUTING_KEY_ORDER_V2, message, correlationData)
    }

    fun sendDirectNotFound(message: OrderMessage, correlationData: CorrelationData){
        rabbitTemplate.convertAndSend(EX_DEV_DIRECT, ROUTING_KEY_ORDER_NOT_FOUND, message, correlationData)
    }

    fun sendPriorityMessage(message: OrderMessage, correlationData: CorrelationData, priority: Int){
        rabbitTemplate.convertAndSend(EX_DEV_DIRECT,
            ROUTING_KEY_ORDER_V3,
            message,
            {m -> m.messageProperties.priority = priority; m;},
            correlationData)
    }

    fun sendDelayMessage(message: OrderMessage, correlationData: CorrelationData, seconds: Int){
        //val now = LocalDateTime.now()
        //var toEpochSecond = now.plusSeconds(seconds.toLong()).atZone(ZoneId.of("UTC")).toEpochSecond()
        //
        rabbitTemplate.convertAndSend(EX_DEV_DIRECT,
            ROUTING_KEY_ORDER_V4,
            message,
            {m -> m.messageProperties.expiration = (seconds * 1000).toString(); m;},
            correlationData)
    }

}