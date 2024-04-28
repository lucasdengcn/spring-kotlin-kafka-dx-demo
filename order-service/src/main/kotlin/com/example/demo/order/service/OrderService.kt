package com.example.demo.order.service

import com.example.demo.domain.ActSource
import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.order.configuration.TOPIC_ORDERS
import com.example.demo.order.entity.OrderEntity
import com.example.demo.order.repository.OrderRepository
import org.apache.kafka.streams.StoreQueryParameters
import org.apache.kafka.streams.state.QueryableStoreTypes
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.config.StreamsBuilderFactoryBean
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Service
class OrderService (
    val orderRepository: OrderRepository,
    val kafkaTemplate: KafkaTemplate<String, Order>,
    val kafkaStreamFactorBean: StreamsBuilderFactoryBean
) {

    companion object {
        val logger : Logger = LoggerFactory.getLogger(OrderService::class.java);
    }

    @Transactional
    fun create(order: Order) : Order {
        //
        val orderEntity = OrderEntity(
            id = null,
            customerId = Random.nextInt(1, 101),
            productId = Random.nextInt(1, 11),
            status = OrderStatus.NEW,
            price = order.price,
            productCount = order.productCount,
            source = ActSource.ORDER
        )
        //
        orderRepository.save(orderEntity);
        //
        order.id = orderEntity.id!!
        sendOrder(order)
        //
        return order;
    }

    @Transactional
    fun confirm(orderPayment: Order, orderStock: Order) : Order {
        val order = Order(
            id = orderPayment.id,
            customerId = orderPayment.customerId,
            productId = orderPayment.productId,
            productCount = orderPayment.productCount,
            price = orderPayment.price
        );

        if (orderPayment.status == OrderStatus.ACCEPT && orderStock.status == OrderStatus.ACCEPT) {
            order.status = OrderStatus.CONFIRMED;
        } else if (orderPayment.status == OrderStatus.REJECT && orderStock.status == OrderStatus.REJECT) {
            val source = if (orderPayment.status == OrderStatus.REJECT) ActSource.PAYMENT else ActSource.STOCK;
            order.status = OrderStatus.REJECTED;
            order.source = source;
        } else if (orderPayment.status == OrderStatus.REJECT || orderStock.status == OrderStatus.REJECT) {
            val source = if (orderPayment.status == OrderStatus.REJECT) ActSource.PAYMENT else ActSource.STOCK;
            order.status = OrderStatus.ROLLBACK;
            order.source = source;
        }
        //
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
            val x = Random.nextInt(10, 1000);
            val orderEntity = OrderEntity(
                id = null,
                customerId = Random.nextInt(1, 101),
                productId = Random.nextInt(1, 11),
                status = OrderStatus.NEW,
                price = 20 * x,
                productCount = x,
                source = ActSource.ORDER
            )
            //
            orderRepository.save(orderEntity);
            //
            val order = Order(
                id = orderEntity.id!!,
                customerId = orderEntity.customerId!!,
                productId = orderEntity.productId!!,
                status = orderEntity.status,
                price = orderEntity.price!!,
                productCount = orderEntity.productCount!!,
                source =  orderEntity.source
            )
            //
            sendOrder(order)
        }
    }

    private fun sendOrder(order: Order) {
        kafkaTemplate.send(TOPIC_ORDERS, order.id.toString(), order);
        logger.info("Send: $order");
    }


    @Transactional
    fun updateStatus(order: Order) {
        val orderEntity = orderRepository.findById(order.id).orElseThrow()
        orderEntity.status = order.status
        orderEntity.source = order.source
        orderRepository.save(orderEntity)
        logger.info("updateStatus: $order");
    }

}