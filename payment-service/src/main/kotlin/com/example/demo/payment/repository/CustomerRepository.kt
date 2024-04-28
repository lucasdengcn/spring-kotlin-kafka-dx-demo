package com.example.demo.payment.repository

import com.example.demo.payment.entity.Customer
import org.springframework.data.repository.CrudRepository

interface CustomerRepository : CrudRepository<Customer, Int> {
}