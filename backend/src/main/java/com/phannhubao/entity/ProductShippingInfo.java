package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_shipping_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductShippingInfo {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(name = "product_id")
    private java.util.UUID productId;

    @Column(nullable = false)
    private Double weight;

    @Column(name = "weight_unit")
    private String weightUnit;

    @Column(nullable = false)
    private Double volume;

    @Column(name = "volume_unit")
    private String volumeUnit;

    @Column(name = "dimension_width", nullable = false)
    private Double dimensionWidth;

    @Column(name = "dimension_height", nullable = false)
    private Double dimensionHeight;

    @Column(name = "dimension_depth", nullable = false)
    private Double dimensionDepth;

    @Column(name = "dimension_unit")
    private String dimensionUnit;
}
