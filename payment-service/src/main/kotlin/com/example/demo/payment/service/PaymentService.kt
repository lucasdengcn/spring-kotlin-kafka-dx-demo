package com.example.demo.payment.service

import com.example.demo.domain.ActSource
import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.payment.entity.Customer
import com.example.demo.payment.repository.CustomerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

private const val TOPIC_PAYMENT = "payment-orders"

@Service
class PaymentService (
    val customerRepository: CustomerRepository,
    val kafkaTemplate: KafkaTemplate<String, Order>)
{

    companion object {
        val logger: Logger = LoggerFactory.getLogger(PaymentService::class.java)
    }

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
        kafkaTemplate.send(TOPIC_PAYMENT, order.id.toString(), order)
        logger.info("Sent: $order")
    }

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
}