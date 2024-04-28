package com.example.demo.domain

/**
 *
 */
data class Order(
    var id: Int,
    val customerId: Int,
    val productId: Int,
    val productCount: Int = 0,
    val price: Int = 0,
    var status: OrderStatus = OrderStatus.NEW,
    var source: ActSource = ActSource.ORDER
)
