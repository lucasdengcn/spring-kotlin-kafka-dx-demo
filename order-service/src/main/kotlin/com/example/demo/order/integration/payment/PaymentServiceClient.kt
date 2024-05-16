package com.example.demo.order.integration.payment

import com.example.demo.order.integration.payment.model.OrderPaymentStatus
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import io.netty.util.concurrent.CompleteFuture
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.service.annotation.GetExchange

interface PaymentServiceClient {
    //    @GetExchange("/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?num=80&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page")
//    List<StockSpotInfo> getQuotes(@RequestParam int page);

    @Retry(name = "payment")
    @CircuitBreaker(name = "payment")
    @GetExchange("/orders/{id}/status")
    fun getPaymentStatus(
        @PathVariable id: Int,
    ): OrderPaymentStatus

    @Retry(name = "payment")
    @CircuitBreaker(name = "payment")
    @GetExchange("/orders/{id}/status")
    fun getPaymentStatus2(
        @PathVariable id: Int,
    ): CompleteFuture<OrderPaymentStatus>

//    @PostExchange("/books")
//    OrderPaymentStatus saveBook(@RequestBody StockSpotInfo book);
//
//    @DeleteExchange("/books/{id}")
//    ResponseEntity<Void> deleteBook(@PathVariable long id);
}
