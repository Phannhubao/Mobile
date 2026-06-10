package com.phannhubao.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_suppliers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ProductSupplierId.class)
public class ProductSupplier {
    // Composite Primary Key using ProductSupplierId

    @Id
    @Column(name = "product_id", nullable = false)
    private java.util.UUID productId;

    @Id
    @Column(name = "supplier_id", nullable = false)
    private java.util.UUID supplierId;
}
