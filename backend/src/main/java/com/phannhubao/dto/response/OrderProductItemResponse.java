package com.phannhubao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductItemResponse {
    private String title;
    private String brand;
    private String color;
    private String size;
    private int units;
    private int price;
    private String imageUrl;
    private String productId;
}
