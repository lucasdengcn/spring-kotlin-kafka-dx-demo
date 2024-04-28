package com.example.demo.order.entity

import com.example.demo.domain.ActSource
import com.example.demo.domain.OrderStatus
import jakarta.persistence.*

@Entity
@Table(name = "orders")
class OrderEntity (

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_orders_id")
    @SequenceGenerator(name = "seq_orders_id", sequenceName = "seq_orders_id", allocationSize = 50)
    var id: Int?,
    val customerId: Int?,
    val productId: Int?,
    val productCount: Int?,
    val price: Int?,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.NEW,

    @Enumerated(EnumType.STRING)
    var source: ActSource = ActSource.ORDER
) {

}