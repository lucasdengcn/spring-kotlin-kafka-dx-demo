package com.example.demo.order.repository

import com.example.demo.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<OrderEntity, Int> {
}