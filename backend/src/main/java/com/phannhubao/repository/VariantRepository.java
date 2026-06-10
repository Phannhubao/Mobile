package com.phannhubao.repository;

import com.phannhubao.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository extends JpaRepository<Variant, java.util.UUID> {
}
