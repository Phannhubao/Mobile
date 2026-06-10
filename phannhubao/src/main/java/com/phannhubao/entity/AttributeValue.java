package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attribute_values")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttributeValue {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private Attribute attribute;

    @Column(name = "attribute_value", nullable = false)
    private String attributeValue;

    private String color;
}
