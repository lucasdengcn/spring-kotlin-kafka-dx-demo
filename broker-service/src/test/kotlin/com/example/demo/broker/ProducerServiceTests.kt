package com.example.demo.broker

import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.beans.factory.annotation.Autowired

import org.springframework.boot.test.context.SpringBootTest
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.util.UUID
import kotlin.random.Random

@SpringBootTest
class ProducerServiceTests (@Autowired val producer: ProducerService) {

    @Test
    fun send() {
        var correlationData = CorrelationData(UUID.randomUUID().toString())
        OrderMessage(1, 10.0, 2, "123456789", LocalDateTime.now(), 0)
            .also { println(it) }
            .let { producer.sendDirectOrder(it, correlationData)}
        Thread.sleep(600 * 1000)
    }

    @Test
    fun sendMultiple() {
        // expect some messages would be dropped.
        (0..2).forEach {
            var correlationData = CorrelationData(it.toString())
            var q = Random.Default.nextInt(1, 200)
            OrderMessage(it + 10, 10.0, q, "123456789", LocalDateTime.now(), 0)
                .also { println(it) }
                .let { producer.sendDirectOrder(it, correlationData)}
            sleep(500)
        }
        //
        Thread.sleep(100 * 1000)
    }

    @Test
    fun sendNotFoundMandatory() {
        // expect invoke returnCallback
        var correlationData = CorrelationData("0")
        //
        OrderMessage(110, 10.0, 1, "123456789", LocalDateTime.now(), 0)
            .also { println(it) }
            .let { producer.sendDirectNotFound(it, correlationData)}
        sleep(10 * 1000)
    }

    @Test
    fun sendNotFoundOptional() {
        // expect message route to alt exchange
        (0..5).forEach {
            var correlationData = CorrelationData(it.toString())
            OrderMessage(200 + it, 10.0, 1, "123456789", LocalDateTime.now(), 0)
                .also { println(it) }
                .let { producer.sendDirectNotFound(it, correlationData)}
        }
        sleep(10 * 1000)
    }

    @Test
    fun sendLargeQuantity() {
        // expect invoke returnCallback
        var correlationData = CorrelationData("0")
        //
        OrderMessage(110, 10.0, 100, "123456789", LocalDateTime.now(), 0)
            .also { println(it) }
            .let { producer.sendDirectOrder(it, correlationData)}
        sleep(10 * 1000)
    }

    @Test
    fun sendWithPriority() {
        // expect invoke returnCallback
        (0..50).forEach {
            var correlationData = CorrelationData(it.toString())
            //
            var priority = Random.Default.nextInt(0, 5)
            OrderMessage(110, 10.0, 100, "123456789", LocalDateTime.now(), priority)
                .also { println(it) }
                .let { producer.sendPriorityMessage(it, correlationData, priority)}
        }
        sleep(10 * 1000)
    }

    @Test
    fun sendWithExpiration() {
        // expect invoke returnCallback
        (0..10).forEach {
            var correlationData = CorrelationData(it.toString())
            //
            var seconds = Random.Default.nextInt(5, 10)
            OrderMessage(110, 10.0, 100, "1", LocalDateTime.now(), seconds)
                .also { println(it) }
                .let { producer.sendDelayMessage(it, correlationData, seconds)}
        }
        sleep(100 * 1000)
    }

}