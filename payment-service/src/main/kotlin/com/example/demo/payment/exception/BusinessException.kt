package com.example.demo.payment.exception

data class BusinessException(override val message: String, override val cause: Throwable? = null) : RuntimeException(message, cause)
