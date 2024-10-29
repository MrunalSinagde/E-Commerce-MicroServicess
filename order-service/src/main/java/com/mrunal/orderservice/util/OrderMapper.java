package com.mrunal.orderservice.util;

import com.mrunal.orderservice.dto.OrderLineItemsDto;
import com.mrunal.orderservice.dto.OrderRequest;
import com.mrunal.orderservice.dto.OrderResponse;
import com.mrunal.orderservice.entity.Order;
import com.mrunal.orderservice.entity.OrderLineItems;
import com.mrunal.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

public class OrderMapper {

    @Autowired
    private static OrderRepository orderRepository;

    public static OrderLineItemsDto mapToDto(OrderLineItems orderLineItems, Long id){
        return OrderLineItemsDto
                .builder()
                .id(orderLineItems.getId())
                .price(orderLineItems.getPrice())
                .quantity(orderLineItems.getQuantity())
                .price(orderLineItems.getPrice())
                .orderId(id)
                .skuCode(orderLineItems.getSkuCode())
                .build();
    }

    public static OrderResponse toResponse(Order order){
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(order.getOrderNumber());
        List<OrderLineItemsDto> orderLineItemsDtoList = order.getOrderLineItemsList()
                .stream()
                .map(orderLineItems -> OrderMapper.mapToDto(orderLineItems, order.getId()))
                .toList();
        orderResponse.setOrderLineItemsList(orderLineItemsDtoList);
        return orderResponse;
    }
}
