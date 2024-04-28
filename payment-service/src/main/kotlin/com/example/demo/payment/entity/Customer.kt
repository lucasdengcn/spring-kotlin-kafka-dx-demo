package com.example.demo.payment.entity

import jakarta.persistence.*

@Entity
class Customer (
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_customer_id")
    @SequenceGenerator(name = "seq_customer_id", sequenceName = "seq_customer_id", allocationSize = 50)
    var id: Int?,
    var name: String,
    var amountAvailable: Int,
    var amountReserved: Int) {
}
