package com.phannhubao.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreateRequest {
    @NotNull
    private UUID productId;

    private String selectedSize;
    private String selectedColor;

    @Min(1)
    private Integer quantity;
}
