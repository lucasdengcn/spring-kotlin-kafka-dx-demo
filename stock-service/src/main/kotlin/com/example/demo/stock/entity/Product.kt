package com.example.demo.stock.entity

import jakarta.persistence.*

@Entity
class Product (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_product_id")
    @SequenceGenerator(name = "seq_product_id", sequenceName = "seq_product_id", allocationSize = 50)
    var id: Int?,
    var name: String,
    var availableItems: Int,
    var reservedItems: Int) {
}
