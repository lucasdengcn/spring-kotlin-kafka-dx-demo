package com.example.demo.order.integration.payment

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
public class PaymentServiceClientConfiguration {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(PaymentServiceClient::class.java)
    }

    @Value("\${PAYMENT_SERVICE_BASE_URL:http://127.0.0.1:8080/api}")
    lateinit var baseUrl: String

    @Bean
    fun paymentServiceClient(): PaymentServiceClient {
        logger.info("paymentServiceClient: $baseUrl")
        var restClient = RestClient.builder().baseUrl(baseUrl).build()
        var adapter = RestClientAdapter.create(restClient)
        var factory = HttpServiceProxyFactory.builderFor(adapter).build()
        var service = factory.createClient(PaymentServiceClient::class.java)
        return service
    }
}
