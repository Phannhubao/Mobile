package com.phannhubao.repository;

import com.phannhubao.entity.ProductShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductShippingInfoRepository extends JpaRepository<ProductShippingInfo, java.util.UUID> {
}
