package com.example.demo.streaming

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class BrokerApplication

fun main(args: Array<String>) {
    runApplication<BrokerApplication>(*args)
}
