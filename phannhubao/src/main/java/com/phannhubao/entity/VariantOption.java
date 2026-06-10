package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "variant_options")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantOption {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(nullable = false)
    private String title;

    @Column(name = "image_id")
    private java.util.UUID imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "sale_price", nullable = false)
    private Double salePrice;

    @Column(name = "compare_price")
    private Double comparePrice;

    @Column(name = "buying_price")
    private Double buyingPrice;

    @Column(nullable = false)
    private Integer quantity;

    private String sku;

    private Boolean active;
}
