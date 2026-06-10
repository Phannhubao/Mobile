package com.phannhubao.dto.response;

import com.phannhubao.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private UUID id;
    private Product product;
    private String selectedSize;
    private String selectedColor;
    private int quantity;
}
