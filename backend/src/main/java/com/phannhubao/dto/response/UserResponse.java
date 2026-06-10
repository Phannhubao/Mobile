package com.phannhubao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String provider;
    private String avatarUrl;
    private Set<String> roles;
    private String dateOfBirth;
    private Boolean salesNotification;
    private Boolean newArrivalsNotification;
    private Boolean deliveryStatusNotification;
}
