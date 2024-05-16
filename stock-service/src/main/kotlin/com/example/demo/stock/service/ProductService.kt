package com.example.demo.stock.service

import com.example.demo.domain.ActSource
import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.stock.entity.Product
import com.example.demo.stock.repository.ProductRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    val productRepository: ProductRepository,
    val kafkaTemplate: KafkaTemplate<String, Order>,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(ProductService::class.java)
    }

    @Transactional("transactionManager")
    fun reserve(order: Order) {
        logger.info("reserve order: $order")
        if (order.status != OrderStatus.NEW) {
            return
        }
        val product = findProduct(order)
        //
        if (order.productCount < product.availableItems) {
            order.status = OrderStatus.ACCEPT
            product.reservedItems += order.productCount
            product.availableItems -= order.productCount
        } else {
            order.status = OrderStatus.REJECT
        }
        order.source = ActSource.STOCK
        //
        productRepository.save(product)
        sendMessage(order)
    }

    @Transactional("kafkaTransactionManager")
    fun sendMessage(order: Order) {
        val future = kafkaTemplate.send("stock-orders", order.id.toString(), order)
        future.whenComplete { t, u -> logger.info("Sent: $order") }
    }

    @Transactional("transactionManager")
    fun confirm(order: Order) {
        logger.info("confirm order: $order")
        val product = findProduct(order)
        //
        if (order.status == OrderStatus.CONFIRMED) {
            product.reservedItems -= order.productCount
            productRepository.save(product)
        } else if (order.status == OrderStatus.ROLLBACK && order.source != ActSource.STOCK) {
            product.reservedItems -= order.productCount
            product.availableItems += order.productCount
            productRepository.save(product)
        }
    }

    private fun findProduct(order: Order): Product {
        val product = productRepository.findById(order.productId).orElseThrow()
        logger.info("Found: $product")
        return product
    }
}
