package com.phannhubao.repository;

import com.phannhubao.entity.ProductSupplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phannhubao.entity.ProductSupplierId;

@Repository
public interface ProductSupplierRepository extends JpaRepository<ProductSupplier, ProductSupplierId> {
}
