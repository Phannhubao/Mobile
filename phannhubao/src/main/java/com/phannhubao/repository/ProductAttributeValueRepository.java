package com.phannhubao.repository;

import com.phannhubao.entity.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, java.util.UUID> {
}
