package com.example.demo.order.service

import com.example.demo.domain.ActSource
import com.example.demo.domain.Order
import com.example.demo.domain.OrderStatus
import com.example.demo.order.configuration.TOPIC_ORDERS
import com.example.demo.order.entity.OrderEntity
import com.example.demo.order.integration.payment.PaymentServiceClient
import com.example.demo.order.integration.payment.model.OrderPaymentStatus
import com.example.demo.order.repository.OrderRepository
import com.example.demo.order.telementry.TraceHolder
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.netty.util.concurrent.CompleteFuture
import io.opentelemetry.instrumentation.annotations.WithSpan
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
class OrderService(
    val orderRepository: OrderRepository,
    val kafkaTemplate: KafkaTemplate<String, Order>,
    val kafkaStreamFactorBean: StreamsBuilderFactoryBean,
    val paymentServiceClient: PaymentServiceClient,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderService::class.java)
    }

    @Transactional("transactionManager")
    fun create(order: Order): Order {
        //
        val orderEntity =
            OrderEntity(
                id = null,
                customerId = Random.nextInt(1, 101),
                productId = Random.nextInt(1, 11),
                status = OrderStatus.NEW,
                price = order.price,
                productCount = order.productCount,
                source = ActSource.ORDER,
            )
        //
        orderRepository.save(orderEntity)
        //
        order.id = orderEntity.id!!
        sendOrderMessage(order)
        //
        if (order.id % 3 == 1) {
            logger.error("transactional kafka and db, $orderEntity")
            throw RuntimeException("transactional kafka and db, id=${orderEntity.id}")
        }
        //
        return order
    }

    @Transactional("transactionManager")
    fun confirm(
        orderPayment: Order,
        orderStock: Order,
    ): Order {
        val order =
            Order(
                id = orderPayment.id,
                customerId = orderPayment.customerId,
                productId = orderPayment.productId,
                productCount = orderPayment.productCount,
                price = orderPayment.price,
            )

        if (orderPayment.status == OrderStatus.ACCEPT && orderStock.status == OrderStatus.ACCEPT) {
            order.status = OrderStatus.CONFIRMED
        } else if (orderPayment.status == OrderStatus.REJECT && orderStock.status == OrderStatus.REJECT) {
            val source = if (orderPayment.status == OrderStatus.REJECT) ActSource.PAYMENT else ActSource.STOCK
            order.status = OrderStatus.REJECTED
            order.source = source
        } else if (orderPayment.status == OrderStatus.REJECT || orderStock.status == OrderStatus.REJECT) {
            val source = if (orderPayment.status == OrderStatus.REJECT) ActSource.PAYMENT else ActSource.STOCK
            order.status = OrderStatus.ROLLBACK
            order.source = source
        }
        //
        logger.info("confirm: payment:${orderPayment.status}, stock:${orderStock.status}, $order")
        return order
    }

    fun all(): List<Order> {
        val keyValueStore = QueryableStoreTypes.keyValueStore<String, Order>()
        val keyValueIterator =
            kafkaStreamFactorBean.kafkaStreams?.store(
                StoreQueryParameters.fromNameAndType(TOPIC_ORDERS, keyValueStore),
            )?.all()
        val orderList = mutableListOf<Order>()
        keyValueIterator?.iterator()?.forEach {
            orderList.add(it.value)
        }
        return orderList
    }

    @Async
    @Transactional("transactionManager")
    fun generate(limit: Int) {
        for (i in 0 until limit) {
            val productCount = Random.nextInt(10, 1000)
            val orderEntity =
                OrderEntity(
                    id = null,
                    customerId = Random.nextInt(1, 101),
                    productId = Random.nextInt(1, 11),
                    status = OrderStatus.NEW,
                    price = 20 * productCount,
                    productCount = productCount,
                    source = ActSource.ORDER,
                )
            //
            orderRepository.save(orderEntity)
            //
            val order =
                Order(
                    id = orderEntity.id!!,
                    customerId = orderEntity.customerId!!,
                    productId = orderEntity.productId!!,
                    status = orderEntity.status,
                    price = orderEntity.price!!,
                    productCount = orderEntity.productCount!!,
                    source = orderEntity.source,
                )
            //
            sendOrderMessage(order)
        }
    }

    @Transactional("kafkaTransactionManager")
    fun sendOrderMessage(order: Order) {
        val future = kafkaTemplate.send(TOPIC_ORDERS, order.id.toString(), order)
        future.whenComplete { t, u -> logger.info("Send: $order") }
        Thread.sleep(1000)
    }

    @Transactional("transactionManager")
    fun updateStatus(order: Order) {
        val orderEntity = orderRepository.findById(order.id).orElseThrow()
        orderEntity.status = order.status
        orderEntity.source = order.source
        orderRepository.save(orderEntity)
        logger.info("updateStatus: $order")
    }

    @WithSpan
    fun getPaymentStatus(id: Int): OrderPaymentStatus? {
        logger.info("trace id: ${TraceHolder.currentTraceId()}")
        val orderEntity = orderRepository.findById(id).orElseThrow()
        return paymentServiceClient.getPaymentStatus(id)
    }

    @Retry(name = "payment")
    @Bulkhead(name = "payment", type = Bulkhead.Type.THREADPOOL)
    // @TimeLimiter(name = "payment")
    @CircuitBreaker(name = "payment")
    fun getPaymentStatusAsync(id: Int): CompleteFuture<OrderPaymentStatus> {
        val orderEntity = orderRepository.findById(id).orElseThrow()
        return paymentServiceClient.getPaymentStatus2(id)
    }
}
