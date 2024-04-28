package com.example.demo.domain

/**
 *
 */
data class Order(
    var id: Int,
    val customerId: String,
    val productId: String,
    val productCount: Int? = null,
    val price: Int? = null,
    var status: String? = "NEW",
    var source: String? = null
)
