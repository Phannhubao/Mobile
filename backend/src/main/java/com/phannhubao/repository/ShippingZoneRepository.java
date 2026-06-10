package com.phannhubao.repository;

import com.phannhubao.entity.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingZoneRepository extends JpaRepository<ShippingZone, java.util.UUID> {
}
