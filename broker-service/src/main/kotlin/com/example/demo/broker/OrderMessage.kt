package com.example.demo.broker

import java.time.LocalDateTime

class OrderMessage(
    val orderId: Int,
    val price: Double,
    val quantity: Int,
    val productId: String,
    val dateTime: LocalDateTime,
    val priority: Int,
) {
    override fun toString(): String {
        return "OrderMessage [orderId=$orderId, price=$price, quantity=$quantity, productId=$productId, dateTime=$dateTime, priority=$priority]"
    }
}
