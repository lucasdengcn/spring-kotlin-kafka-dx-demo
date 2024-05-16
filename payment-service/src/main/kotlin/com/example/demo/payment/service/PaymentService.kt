package com.example.demo.payment.service

import com.example.demo.domain.ActSource
import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.payment.entity.Customer
import com.example.demo.payment.exception.BusinessException
import com.example.demo.payment.model.OrderPaymentStatus
import com.example.demo.payment.repository.CustomerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.atomic.AtomicInteger

private const val TOPIC_PAYMENT = "payment-orders"

@Service
class PaymentService(
    val customerRepository: CustomerRepository,
    val kafkaTemplate: KafkaTemplate<String, Order>,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(PaymentService::class.java)
        val counter: AtomicInteger = AtomicInteger()
    }

    @Transactional("transactionManager")
    fun reserve(order: Order) {
        logger.info("reserve order: $order")
        val customer = findCustomer(order)
        //
        if (order.price < customer.amountAvailable) {
            order.status = OrderStatus.ACCEPT
            customer.amountReserved += order.price
            customer.amountAvailable -= order.price
        } else {
            order.status = OrderStatus.REJECT
        }
        order.source = ActSource.PAYMENT
        //
        customerRepository.save(customer)
        sendMessage(order)
    }

    @Transactional("kafkaTransactionManager")
    fun sendMessage(order: Order) {
        val future = kafkaTemplate.send(TOPIC_PAYMENT, order.id.toString(), order)
        future.whenComplete { t, u -> logger.info("Sent: $order") }
    }

    @Transactional("transactionManager")
    fun confirm(order: Order) {
        logger.info("confirm order: $order")
        val customer = findCustomer(order)
        //
        if (order.status == OrderStatus.CONFIRMED) {
            customer.amountReserved -= order.price
            customerRepository.save(customer)
        } else if (order.status == OrderStatus.ROLLBACK && order.source != ActSource.PAYMENT) {
            customer.amountReserved -= order.price
            customer.amountAvailable += order.price
            customerRepository.save(customer)
        }
    }

    private fun findCustomer(order: Order): Customer {
        val customer = customerRepository.findById(order.customerId).orElseThrow()
        logger.info("Found: $customer")
        return customer
    }

    fun getPaymentStatusByOrderId(id: Int): OrderPaymentStatus? {
        if (id % 3 == 0) {
            throw BusinessException("order id $id not found")
        }
        val count = counter.incrementAndGet()
        logger.info("getPaymentStatusByOrderId: $id, $count")
        if (count % 3 == 0) {
            throw BusinessException("order id $id: unexpected error")
        }
        if (id % 5 == 0) {
            return OrderPaymentStatus(id, 0, "Failed")
        }
        if (id % 2 == 0) {
            return OrderPaymentStatus(id, 1, "Ongoing")
        }
        return OrderPaymentStatus(id, 2, "Payed")
    }
}
