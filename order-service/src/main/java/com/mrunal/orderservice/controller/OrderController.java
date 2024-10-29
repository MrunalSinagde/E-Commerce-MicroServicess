package com.mrunal.orderservice.controller;

import com.mrunal.orderservice.dto.OrderRequest;
import com.mrunal.orderservice.dto.OrderResponse;
import com.mrunal.orderservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    public CompletableFuture<ResponseEntity<OrderResponse>> placeOrder(@RequestBody OrderRequest orderRequest){
        return orderService.placeOrder(orderRequest)
                .thenApply(orderResponse -> new ResponseEntity<>(orderResponse, HttpStatus.CREATED));
    }

    public CompletableFuture<ResponseEntity<String>> fallbackMethod(OrderRequest orderRequest, RuntimeException exception){
        return CompletableFuture.supplyAsync(() -> new ResponseEntity<>("oops something went wrong, please try again after some time!!", HttpStatus.SERVICE_UNAVAILABLE));
    }
}
