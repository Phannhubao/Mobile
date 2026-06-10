package com.phannhubao.repository;

import com.phannhubao.entity.VariantValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantValueRepository extends JpaRepository<VariantValue, java.util.UUID> {
}
