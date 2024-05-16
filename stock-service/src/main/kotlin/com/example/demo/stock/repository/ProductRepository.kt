package com.example.demo.stock.repository

import com.example.demo.stock.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Int>
