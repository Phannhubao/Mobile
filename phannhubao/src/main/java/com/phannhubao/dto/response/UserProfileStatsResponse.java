package com.phannhubao.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileStatsResponse {
    private long orderCount;
    private long addressCount;
    private String paymentMethodSummary;
    private long couponCount;
    private long reviewCount;
}
