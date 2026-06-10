package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_attribute_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeValue {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id", nullable = false)
    private ProductAttribute productAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", nullable = false)
    private AttributeValue attributeValue;
}
