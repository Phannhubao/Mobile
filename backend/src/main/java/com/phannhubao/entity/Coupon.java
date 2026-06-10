package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_value")
    private Double discountValue;

    @Column(name = "discount_type", nullable = false, length = 50)
    private String discountType;

    @Column(name = "times_used", nullable = false)
    @Builder.Default
    private Double timesUsed = 0.0;

    @Column(name = "max_usage")
    private Double maxUsage;

    @Column(name = "order_amount_limit")
    private Double orderAmountLimit;

    @Column(name = "coupon_start_date")
    private OffsetDateTime couponStartDate;

    @Column(name = "coupon_end_date")
    private OffsetDateTime couponEndDate;
}
