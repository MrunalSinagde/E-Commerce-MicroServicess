package com.mrunal.orderservice.service;

import com.mrunal.orderservice.dto.InventoryStockResponse;
import com.mrunal.orderservice.dto.OrderLineItemsDto;
import com.mrunal.orderservice.dto.OrderRequest;
import com.mrunal.orderservice.dto.OrderResponse;
import com.mrunal.orderservice.entity.Order;
import com.mrunal.orderservice.entity.OrderLineItems;
import com.mrunal.orderservice.event.OrderPlacedEvent;
import com.mrunal.orderservice.repository.OrderRepository;
import com.mrunal.orderservice.util.OrderMapper;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private WebClient.Builder webClient;
    @Autowired
    private Tracer tracer;
    @Autowired
    private KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Transactional
    @TimeLimiter(name = "inventory")
    public CompletableFuture<OrderResponse> placeOrder(OrderRequest orderRequest){

        return CompletableFuture.supplyAsync(() -> {
            Order order = new Order();
            order.setOrderNumber(UUID.randomUUID().toString());

            List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsList()
                    .stream()
                    .map(this::fromDto)
                    .toList();

            orderLineItemsList.forEach(orderLineItems -> orderLineItems.setOrder(order));

            order.setOrderLineItemsList(orderLineItemsList);
            List<String> skuCodes = orderLineItemsList.stream()
                    .map(OrderLineItems::getSkuCode)
                    .toList();

            Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup");

            try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){
                return webClient.build().get()
                        .uri("http://inventory-service/api/inventory/isInStock",
                                uriBuilder -> uriBuilder.queryParam("sku-code", skuCodes).build())
                        .retrieve()
                        .bodyToMono(InventoryStockResponse[].class)
                        .toFuture() // Make it async
                        .thenApply(inventoryStockResponseArray -> {

                            boolean allProductIsInStock = Arrays.stream(inventoryStockResponseArray)
                                    .allMatch(InventoryStockResponse::getIsInStock);

                            if (allProductIsInStock) {
                                orderRepository.save(order);
//                                orderLineItemsList.forEach(orderLineItems -> orderLineItems.setOrder(order));
                                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                                return OrderMapper.toResponse(order);
                            } else {
                                throw new RuntimeException("Product is not in stock");
                            }
                        }).join();
            }finally {
                inventoryServiceLookup.end();
            }
        });
    }

    public OrderLineItems fromDto(OrderLineItemsDto orderLineItemsDto){
        return OrderLineItems
                .builder()
                .price(orderLineItemsDto.getPrice())
                .skuCode(orderLineItemsDto.getSkuCode())
                .quantity(orderLineItemsDto.getQuantity())
                .build();
    }
}
