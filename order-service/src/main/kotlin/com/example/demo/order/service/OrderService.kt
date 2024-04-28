package com.example.demo.order.service

import com.example.demo.domain.Order
import com.example.demo.order.configuration.TOPIC_ORDERS
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

@Service
class OrderService (
    val kafkaTemplate: KafkaTemplate<String, Order>,
    val kafkaStreamFactorBean: StreamsBuilderFactoryBean
) {

    companion object {
        val id = AtomicInteger();
        val logger = LoggerFactory.getLogger(OrderService::class.java);
        val customerIds = listOf("C001", "C002", "C003", "C004");
        val productIds = listOf("P100", "P101", "P102", "P103")
    }

    fun create(order: Order) : Order {
        order.id = id.incrementAndGet();
        sendOrder(order)
        return order;
    }

    fun confirm(orderPayment: Order, orderStock: Order) : Order {
        val order = Order(
            id = orderPayment.id,
            customerId = orderPayment.customerId,
            productId = orderPayment.productId,
            productCount = orderPayment.productCount,
            price = orderPayment.price
        );

        if (orderPayment.status.equals("ACCEPT") && orderStock.status.equals("ACCEPT")) {
            order.status = "CONFIRMED";
        } else if (orderPayment.status.equals("REJECT") && orderStock.status.equals("REJECT")) {
            order.status = "REJECTED";
        } else if (orderPayment.status.equals("REJECT") || orderStock.status.equals("REJECT")) {
            val source = if (orderPayment.status.equals("REJECT")) "PAYMENT" else "STOCK";
            order.status = "ROLLBACK";
            order.source = source;
        }

        logger.info("confirm: payment:${orderPayment.status}, stock:${orderStock.status}, $order");
        return order;
    }

    fun all() : List<Order> {
        val keyValueStore = QueryableStoreTypes.keyValueStore<String, Order>()
        val keyValueIterator = kafkaStreamFactorBean.kafkaStreams?.store(
            StoreQueryParameters.fromNameAndType(TOPIC_ORDERS, keyValueStore)
        )?.all();
        val orderList = mutableListOf<Order>();
        keyValueIterator?.iterator()?.forEach {
            orderList.add(it.value);
        }
        return orderList;
    }

    @Async
    fun generate(limit: Int) {
        for (i in 0 until limit) {
            val x = Random.nextInt(5) + 1;
            val order = Order(
                id = id.incrementAndGet(),
                customerId = customerIds.random(),
                productId = productIds.random(),
                status = "NEW",
                price = 100 * x,
                productCount = x
            )
            sendOrder(order)
        }
    }

    private fun sendOrder(order: Order) {
        kafkaTemplate.send(TOPIC_ORDERS, order.id.toString(), order);
        logger.info("Send: $order");
    }

}