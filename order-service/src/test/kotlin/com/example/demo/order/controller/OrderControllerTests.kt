package com.example.demo.order.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTests (@Autowired val mockMvc: MockMvc,
                            @Autowired val objectMapper: ObjectMapper
) {

    @Test
    fun `get payment status via orderId return ongoing`() {
        //
        val id = 452
        //
        mockMvc.get("/orders/${id}/payment/status") {
            accept = MediaType.APPLICATION_JSON
        }.andDo { println() }
            .andExpect {
                status { isOk() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.orderId") { value(id) }
            }

    }


}