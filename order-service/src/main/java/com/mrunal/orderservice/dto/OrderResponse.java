package com.mrunal.orderservice.dto;

import com.mrunal.orderservice.entity.OrderLineItems;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private String orderNumber;
    private List<OrderLineItemsDto> orderLineItemsList;
}
