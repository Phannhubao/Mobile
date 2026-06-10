package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sells")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sell {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private java.util.UUID id;

    @Column(name = "product_id")
    private java.util.UUID productId;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer quantity;
}
