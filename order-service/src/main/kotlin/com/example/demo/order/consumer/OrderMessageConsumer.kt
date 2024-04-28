package com.example.demo.order.consumer

import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.order.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderMessageConsumer (val orderService: OrderService) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderMessageConsumer::class.java)
    }

    @KafkaListener(topics = ["orders-finished"], concurrency = "3")
    @Transactional("transactionManager")
    fun onEvent(order: Order) {
        logger.info("Event Received: $order")
        when (order.status){
            OrderStatus.CONFIRMED -> orderService.updateStatus(order)
            OrderStatus.REJECTED -> orderService.updateStatus(order)
            OrderStatus.ROLLBACK -> orderService.updateStatus(order)
            else -> logger.info("incorrect status: $order")
        }
    }

}