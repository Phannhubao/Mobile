package com.phannhubao.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.util.Set;

@Data
public class ProductCreateRequest {
    @NotBlank(message = "Product name is required")
    private String productName;
    private String brandName;
    private String imageUrl;
    @PositiveOrZero(message = "Sale price must be zero or greater")
    private Double salePrice;
    @PositiveOrZero(message = "Compare price must be zero or greater")
    private Double comparePrice;
    @PositiveOrZero(message = "Quantity must be zero or greater")
    private int quantity = 50;
    private String shortDescription;
    private String productDescription;
    private String productType = "simple";
    private Set<String> tags;
    private Set<String> sizes;
    private Set<String> colors;
    private Set<String> categoryIds;
}
