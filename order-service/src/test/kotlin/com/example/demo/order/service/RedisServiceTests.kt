package com.example.demo.order.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.RedisTemplate
import java.time.LocalDate

// @Disabled
@SpringBootTest
class RedisServiceTests(
    @Autowired val redisTemplate: RedisTemplate<String, Any>,
) {
    @Test
    fun `set string`() {
        redisTemplate.opsForValue().set("foo2", "bar12")
    }

    @Test
    fun `get string`() {
        val get = redisTemplate.opsForValue().get("foo2")
        Assertions.assertEquals("bar12", get)
    }

    @Test
    fun `set date`() {
        redisTemplate.opsForValue().set("date0", LocalDate.now())
    }

    @Test
    fun `get date`() {
        val date0 = redisTemplate.opsForValue().get("date0")
        println(date0)
    }
}
