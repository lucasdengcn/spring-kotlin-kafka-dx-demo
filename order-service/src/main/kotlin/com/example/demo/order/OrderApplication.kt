package com.example.demo.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
@EnableKafkaStreams
class OrderApplication

fun main(args: Array<String>) {
    runApplication<OrderApplication>(*args)
}
