package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "shipping_zones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingZone {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    private Boolean active;

    @Column(name = "free_shipping")
    private Boolean freeShipping;

    @Column(name = "rate_type")
    private String rateType;

    @Column(name = "created_at", nullable = false)
    private java.time.OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private java.time.OffsetDateTime updatedAt;

    @Column(name = "created_by")
    private java.util.UUID createdBy;

    @Column(name = "updated_by")
    private java.util.UUID updatedBy;
}
