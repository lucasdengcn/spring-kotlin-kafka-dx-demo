package com.example.demo.payment.repository

import com.example.demo.payment.entity.Customer
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerRepository : JpaRepository<Customer, Int> {
}