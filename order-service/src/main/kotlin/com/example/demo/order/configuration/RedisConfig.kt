package com.example.demo.order.configuration

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.lettuce.core.ReadFrom
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisSentinelConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.Duration

@Configuration
@EnableCaching
class RedisConfig {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(RedisConfig::class.java)
    }

    @Bean
    fun redisConnectionFactory(redisProperties: RedisProperties): LettuceConnectionFactory {
        //
        logger.info("Redis Properties: {}", redisProperties)
        //
        val clientConfig = LettuceClientConfiguration.builder().readFrom(ReadFrom.REPLICA_PREFERRED).build()
        //
        val sentinelConfig: RedisSentinelConfiguration =
            RedisSentinelConfiguration().master(redisProperties.getSentinel().getMaster())
        redisProperties.getSentinel().getNodes().forEach { s ->
            sentinelConfig.sentinel(
                redisProperties.host,
                Integer.valueOf(s),
            )
        }
        // sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()))
        return LettuceConnectionFactory(sentinelConfig, clientConfig)
    }

    @Bean
    fun cacheManager(redisProperties: RedisProperties): CacheManager {
        val redisCacheConfiguration: RedisCacheConfiguration =
            RedisCacheConfiguration.defaultCacheConfig().disableCachingNullValues().entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer<Any>(RedisSerializer.json()))
        redisCacheConfiguration.usePrefix()
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory(redisProperties))
            .cacheDefaults(redisCacheConfiguration).build()
    }

    @Primary
    @Bean
    fun redisTemplate(redisProperties: RedisProperties): RedisTemplate<String, Any> {
        //
        val objectMapper =
            Jackson2ObjectMapperBuilder().failOnEmptyBeans(false)
                .failOnUnknownProperties(false)
                .indentOutput(false)
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .modules( // Optional
                    Jdk8Module(), // Dates/Times
                    JavaTimeModule(),
                )
                .featuresToDisable(
                    SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS,
                    SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS,
                ).build<ObjectMapper>()
        //
        objectMapper.activateDefaultTyping(objectMapper.polymorphicTypeValidator, ObjectMapper.DefaultTyping.NON_FINAL)
        //
        val genericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer(objectMapper)
        //
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer
        redisTemplate.connectionFactory = redisConnectionFactory(redisProperties)
        return redisTemplate
    }
}
