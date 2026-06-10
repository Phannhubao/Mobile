package com.phannhubao.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {
    private String name;
    private String dateOfBirth; // yyyy-MM-dd format
    private Boolean salesNotification;
    private Boolean newArrivalsNotification;
    private Boolean deliveryStatusNotification;
}
