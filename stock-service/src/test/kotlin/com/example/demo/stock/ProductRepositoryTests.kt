package com.example.demo.stock

import com.example.demo.stock.entity.Product
import com.example.demo.stock.repository.ProductRepository
import net.datafaker.Faker
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.random.Random

@SpringBootTest
@ActiveProfiles("test")
class ProductRepositoryTests (@Autowired val productRepository: ProductRepository) {

    @Test
    fun generateProducts(){
        val faker = Faker();
        val products = mutableListOf<Product>();
        for (i in 0 until 10) {
            val count = Random.nextInt(100, 1000)
            var title = faker.book().title()
            products.add(Product(null, title, count, 0))
        }
        productRepository.saveAll(products)
    }

}