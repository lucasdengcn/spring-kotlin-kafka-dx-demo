package com.example.demo.order.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@Disabled
@SpringBootTest
class OrderServiceTest(
    @Autowired val orderService: OrderService,
) {
    @Test
    fun `get payment status via orderId throw error`() {
        assertThrows<NoSuchElementException> { orderService.getPaymentStatus(3) }
    }

    @Test
    fun `get payment status via orderId return ongoing`() {
        val paymentStatus = orderService.getPaymentStatus(452)
        Assertions.assertNotNull(paymentStatus)
        Assertions.assertEquals("Ongoing", paymentStatus?.paymentStatus)
    }

    @Disabled
    @Test
    fun `get payment status via orderId return ongoing Async`() {
        val paymentStatus = orderService.getPaymentStatusAsync(452)
        Assertions.assertNotNull(paymentStatus)
        Assertions.assertEquals("Ongoing", paymentStatus.get()?.paymentStatus)
    }

    @Test
    fun `get payment status via orderId return failed`() {
        var paymentStatus = orderService.getPaymentStatus(510)
        Assertions.assertNotNull(paymentStatus)
        Assertions.assertEquals("Failed", paymentStatus?.paymentStatus)
    }

    @Test
    fun `get payment status via orderId return success`() {
        val paymentStatus = orderService.getPaymentStatus(511)
        Assertions.assertNotNull(paymentStatus)
        Assertions.assertEquals("Payed", paymentStatus?.paymentStatus)
    }
}
