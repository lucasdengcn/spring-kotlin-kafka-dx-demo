package com.example.demo.payment

import com.example.demo.payment.entity.Customer
import com.example.demo.payment.repository.CustomerRepository
import net.datafaker.Faker
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
class CustomerRepositoryTests (@Autowired val customerRepository: CustomerRepository) {

    @Test
    fun generateCustomers(){
        val faker = Faker();
        val customers = mutableListOf<Customer>();
        for (i in 0 until 100) {
            val count = Random.nextInt(100, 1000)
            var fullName = faker.name().fullName()
            if (fullName.length > 100){
                fullName = fullName.substring(0, 100)
            }
            customers.add(Customer(null, fullName, count, 0))
        }
        customerRepository.saveAll(customers)
    }

}