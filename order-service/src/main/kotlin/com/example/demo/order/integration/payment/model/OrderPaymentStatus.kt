package com.example.demo.order.integration.payment.model

data class OrderPaymentStatus(
    val orderId: Int,
    val paymentId: Int,
    val paymentStatus: String,
)
