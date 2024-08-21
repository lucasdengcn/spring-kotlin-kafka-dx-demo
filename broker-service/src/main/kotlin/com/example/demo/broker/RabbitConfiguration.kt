package com.example.demo.broker

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.autoconfigure.amqp.RabbitProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class RabbitConfiguration {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(RabbitConfiguration::class.java)
    }

    @Bean
    fun jsonMessageConverter(objectMapper: ObjectMapper): Jackson2JsonMessageConverter {
        val jsonConverter = Jackson2JsonMessageConverter(objectMapper)
        // jsonConverter.setClassMapper(classMapper())
        return jsonConverter
    }

    @Bean
    @Primary
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        rabbitProperties: RabbitProperties,
        messageConverter: Jackson2JsonMessageConverter,
    ): RabbitTemplate {
        //
        var template = RabbitTemplate(connectionFactory)
        template.setMandatory(rabbitProperties.template.mandatory)
        template.messageConverter = messageConverter
        //
        template.setConfirmCallback { correlationData, ack, cause ->
            if (ack)
                {
                    logger.info("confirm callback on ACK: $correlationData, $cause")
                } else {
                logger.error("confirm callback on NACK: $correlationData, $cause")
            }
        }
        //
        template.setReturnsCallback { message ->
            logger.info("returned message: $message")
        }
        //
        return template
    }
}
