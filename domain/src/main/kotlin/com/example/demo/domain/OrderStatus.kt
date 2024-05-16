package com.example.demo.domain

enum class OrderStatus(val value: String) {
    NEW("NEW"),
    ACCEPT("ACCEPT"),
    REJECT("REJECT"),
    REJECTED("REJECTED"),
    CONFIRMED("CONFIRMED"),
    ROLLBACK("ROLLBACK"),
}
