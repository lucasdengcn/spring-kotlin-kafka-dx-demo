package com.example.demo.order.integration.payment;

import com.example.demo.order.integration.payment.model.OrderPaymentStatus;
import com.example.demo.order.integration.payment.model.StockSpotInfo;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.netty.util.concurrent.CompleteFuture
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

interface PaymentServiceClient {

//    @GetExchange("/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?num=80&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page")
//    List<StockSpotInfo> getQuotes(@RequestParam int page);


    @Retry(name = "payment")
    @CircuitBreaker(name = "payment")
    @GetExchange("/orders/{id}/status")
    fun getPaymentStatus(@PathVariable id : Int) : OrderPaymentStatus;

    @Retry(name = "payment")
    @CircuitBreaker(name = "payment")
    @GetExchange("/orders/{id}/status")
    fun getPaymentStatus2(@PathVariable id : Int) : CompleteFuture<OrderPaymentStatus>;

//    @PostExchange("/books")
//    OrderPaymentStatus saveBook(@RequestBody StockSpotInfo book);
//
//    @DeleteExchange("/books/{id}")
//    ResponseEntity<Void> deleteBook(@PathVariable long id);

}
