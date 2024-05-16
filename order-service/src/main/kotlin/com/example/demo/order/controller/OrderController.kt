package com.example.demo.order.controller

import com.example.demo.domain.Order
import com.example.demo.order.integration.payment.model.OrderPaymentStatus
import com.example.demo.order.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class OrderController(val orderService: OrderService) {
    @PostMapping("/0")
    fun create(
        @RequestBody orderRequest: Order,
    ): ResponseEntity<Order> {
        val order = orderService.create(orderRequest)
        return ResponseEntity.ok(order)
    }

    @PostMapping("/{limit}")
    fun generate(
        @PathVariable limit: Int,
    ): ResponseEntity<Int> {
        orderService.generate(limit)
        return ResponseEntity.ok(limit)
    }

    @GetMapping("/")
    fun all(): ResponseEntity<List<Order>> {
        return ResponseEntity.ok(orderService.all())
    }

    @GetMapping("/{id}/payment/status")
    fun getPaymentStatus(
        @PathVariable id: Int,
    ): ResponseEntity<OrderPaymentStatus> {
        return ResponseEntity.ok(orderService.getPaymentStatus(id))
    }
}
