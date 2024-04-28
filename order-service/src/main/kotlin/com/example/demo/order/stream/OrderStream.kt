package com.example.demo.order.stream

import com.example.demo.domain.Order
import com.example.demo.order.configuration.TOPIC_ORDERS
import com.example.demo.order.configuration.TOPIC_PAYMENT_ORDERS
import com.example.demo.order.configuration.TOPIC_STOCK_ORDERS
import com.example.demo.order.service.OrderService
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.*
import org.apache.kafka.streams.state.Stores
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerde
import java.time.Duration


@Configuration
class OrderStream {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(OrderStream::class.java);
        val orderValueSerde = JsonSerde<Order>(Order::class.java)
        // val keySerde: Serde<String> = Serdes.String();
        val keySerde: Serde<String> = JsonSerde<String>(String::class.java)
    }

    @Bean
    fun stream(builder: StreamsBuilder, orderService: OrderService) : KStream<String, Order> {
        //
        orderValueSerde.configure(mapOf(JsonDeserializer.TRUSTED_PACKAGES to "*"), true)
        keySerde.configure(mapOf(JsonDeserializer.TRUSTED_PACKAGES to "*"), true)
        //
        val stream = builder.stream<String, Order>(TOPIC_PAYMENT_ORDERS,
            Consumed.with(keySerde, orderValueSerde))
        //
        stream.join(
            builder.stream(TOPIC_STOCK_ORDERS),
            orderService::confirm,
            JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofSeconds(10)),
            StreamJoined.with(keySerde, orderValueSerde, orderValueSerde)
        ).peek { key, value ->  logger.info("Output: $value")}
            .to(TOPIC_ORDERS)
        //
        return stream;
    }


    @Bean
    fun table(builder: StreamsBuilder) : KTable<String, Order> {
        val store = Stores.persistentKeyValueStore(TOPIC_ORDERS)
        val stream = builder.stream<String, Order>(TOPIC_ORDERS, Consumed.with(keySerde, orderValueSerde));
        val materialized = Materialized.`as`<String?, Order?>(store).withKeySerde(keySerde).withValueSerde(
            orderValueSerde
        )
        return stream.toTable(materialized);
    }

}