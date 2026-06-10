package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCoupon {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(name = "product_id", nullable = false)
    private java.util.UUID productId;

    @Column(name = "coupon_id", nullable = false)
    private java.util.UUID couponId;
}
