package com.example.demo.order.exception

data class BusinessException(
    override val message: String? = "Business Exception",
    override val cause: Throwable? = null,
) : RuntimeException(
        message,
        cause,
    )
