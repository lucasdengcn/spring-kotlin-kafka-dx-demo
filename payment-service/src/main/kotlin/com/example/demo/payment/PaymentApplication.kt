package com.example.demo.payment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentApplication

fun main(args: Array<String>) {
	runApplication<PaymentApplication>(*args)
}
