package com.phannhubao.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateRequest {
    @Min(1)
    private int quantity;
}
