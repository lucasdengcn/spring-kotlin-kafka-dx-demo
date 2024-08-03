package com.example.demo.broker

import com.rabbitmq.client.ConfirmListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class UuidConfirmListener() : ConfirmListener {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(UuidConfirmListener::class.java)
    }

    override fun handleAck(deliveryTag: Long, multiple: Boolean) {
        //TODO("Not yet implemented")
        logger.info("Ack: {}, {}", deliveryTag, multiple)
    }

    override fun handleNack(deliveryTag: Long, multiple: Boolean) {
        TODO("Not yet implemented")
        logger.info("Nack: {}, {}", deliveryTag, multiple)
    }

}
