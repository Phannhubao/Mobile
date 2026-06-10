package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_rates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingRate {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(name = "shipping_zone_id", nullable = false)
    private java.util.UUID shippingZoneId;

    @Column(name = "weight_unit")
    private String weightUnit;

    @Column(name = "min_value", nullable = false)
    private Double minValue;

    @Column(name = "max_value")
    private Double maxValue;

    @Column(name = "no_max")
    private Boolean noMax;

    @Column(nullable = false)
    private Double price;
}
