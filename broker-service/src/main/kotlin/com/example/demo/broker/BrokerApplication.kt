package com.example.demo.broker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class BrokerApplication {
}

fun main(args: Array<String>) {
    runApplication<BrokerApplication>(*args)
}
