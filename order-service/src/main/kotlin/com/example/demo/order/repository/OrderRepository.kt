package com.example.demo.order.repository

import com.example.demo.order.entity.OrderEntity
import org.springframework.data.repository.CrudRepository

interface OrderRepository : CrudRepository<OrderEntity, Int> {
}