package com.example.demo.stock.consumer

import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.stock.service.ProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderMessageConsumer (val productService: ProductService) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderMessageConsumer::class.java)
    }

    @KafkaListener(id = "orders", topics = ["orders"])
    fun onEvent(order: Order) {
        logger.info("Event Received: $order")
        when (order.status){
            OrderStatus.NEW -> productService.reserve(order)
            else -> productService.confirm(order)
        }
    }

}