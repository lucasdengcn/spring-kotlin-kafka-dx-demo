package com.example.demo.payment.controller

import com.example.demo.payment.model.OrderPaymentStatus
import com.example.demo.payment.service.PaymentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderPaymentController(val paymentService: PaymentService) {
    @GetMapping("/{id}/status")
    fun getPaymentStatus(
        @PathVariable id: Int,
    ): ResponseEntity<OrderPaymentStatus> {
        return ResponseEntity.ok(paymentService.getPaymentStatusByOrderId(id))
    }
}
