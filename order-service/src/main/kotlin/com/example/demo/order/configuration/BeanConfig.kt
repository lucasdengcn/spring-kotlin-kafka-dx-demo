package com.example.demo.order.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class BeanConfig {
    @Bean
    fun taskExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.maxPoolSize = 5
        executor.corePoolSize = 5
        executor.setThreadNamePrefix("kafkaSender-")
        executor.initialize()
        return executor
    }
}
