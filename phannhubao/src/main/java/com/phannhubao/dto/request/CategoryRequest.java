package com.phannhubao.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class CategoryRequest {
    @NotBlank(message = "Category name is required")
    private String categoryName;
    private UUID parentId;
    private String categoryDescription;
    private String icon;
    private String image;
    private String placeholder;
    private Boolean active = true;
}
