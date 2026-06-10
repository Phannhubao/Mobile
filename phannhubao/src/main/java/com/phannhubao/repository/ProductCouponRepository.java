package com.phannhubao.repository;

import com.phannhubao.entity.ProductCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCouponRepository extends JpaRepository<ProductCoupon, java.util.UUID> {
}
