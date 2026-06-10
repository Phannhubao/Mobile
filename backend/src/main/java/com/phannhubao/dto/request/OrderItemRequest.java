package com.phannhubao.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequest {
    private UUID productId;
    private int quantity;
    private String selectedSize;
    private String selectedColor;
    private double price;
}
