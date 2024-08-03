package com.example.demo.broker

import com.rabbitmq.client.Channel
import com.rabbitmq.client.ConfirmListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException


const val EX_DEV_DIRECT = "ex-dev-direct"

class UuidPublisher(private val channel: Channel, private val queue: String) {

    init {
        // Starting Confirm Mode on a Channel
        channel.confirmSelect()
        channel.addConfirmListener(UuidConfirmListener())
    }

    @Throws(IOException::class)
    fun send(message: String) {
        channel.basicPublish(EX_DEV_DIRECT, queue, null, message.toByteArray())
        channel.waitForConfirms(1000);
    }

    @Throws(Exception::class)
    fun sendAllOrDie(messages: List<String>) {
        for (message in messages) {
            channel.basicPublish(EX_DEV_DIRECT, queue, null, message.toByteArray())
        }
        channel.waitForConfirmsOrDie(1000)
    }

}