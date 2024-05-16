package com.example.demo.stock.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator

@Entity
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_product_id")
    @SequenceGenerator(name = "seq_product_id", sequenceName = "seq_product_id", allocationSize = 50)
    var id: Int?,
    var name: String,
    var availableItems: Int,
    var reservedItems: Int,
)
