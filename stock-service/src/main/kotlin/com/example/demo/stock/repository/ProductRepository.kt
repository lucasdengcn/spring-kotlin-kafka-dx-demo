package com.example.demo.stock.repository

import com.example.demo.stock.entity.Product
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<Product, Int> {
}