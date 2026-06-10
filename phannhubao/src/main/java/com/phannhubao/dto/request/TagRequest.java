package com.phannhubao.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {
    @NotBlank(message = "Tag name is required")
    private String tagName;
    private String icon;
}
