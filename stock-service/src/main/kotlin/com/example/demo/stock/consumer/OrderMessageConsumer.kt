package com.example.demo.stock.consumer

import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.stock.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderMessageConsumer(val productService: ProductService) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderMessageConsumer::class.java)
    }

    @KafkaListener(topics = ["orders", "orders-finished"], concurrency = "3")
    @Transactional("transactionManager")
    fun onEvent(order: Order) {
        logger.info("Event Received: $order")
        when (order.status) {
            OrderStatus.NEW -> productService.reserve(order)
            else -> productService.confirm(order)
        }
    }
}
