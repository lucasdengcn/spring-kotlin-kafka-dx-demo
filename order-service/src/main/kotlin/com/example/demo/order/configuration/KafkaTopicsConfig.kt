package com.example.demo.order.configuration

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder


const val TOPIC_ORDERS = "orders"

const val TOPIC_PAYMENT_ORDERS = "payment-orders"

const val TOPIC_STOCK_ORDERS = "stock-orders"

const val TOPIC_ORDERS_FINISHED = "orders-finished"

@Configuration
class KafkaTopicsConfig {

    @Bean
    fun orders() : NewTopic {
        return TopicBuilder.name(TOPIC_ORDERS).partitions(3).compact().build();
    }

    @Bean
    fun ordersFinished() : NewTopic {
        return TopicBuilder.name(TOPIC_ORDERS_FINISHED).partitions(3).compact().build();
    }

    @Bean
    fun paymentOrders() : NewTopic {
        return TopicBuilder.name(TOPIC_PAYMENT_ORDERS).partitions(3).compact().build();
    }

    @Bean
    fun stockOrders() : NewTopic {
        return TopicBuilder.name(TOPIC_STOCK_ORDERS).partitions(3).compact().build();
    }

}