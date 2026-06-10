package com.phannhubao.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {
    private Double totalAmount;
    private String shippingAddress;
    private String deliveryMethod;
    private String paymentMethod;
    private Double discount;
    private List<OrderItemRequest> items;
}
