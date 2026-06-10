package com.phannhubao.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class CategoryResponse {
    private UUID id;
    private UUID parentId;
    private String categoryName;
    private String categoryDescription;
    private String icon;
    private String image;
    private String placeholder;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
