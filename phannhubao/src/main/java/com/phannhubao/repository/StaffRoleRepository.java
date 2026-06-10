package com.phannhubao.repository;

import com.phannhubao.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRoleRepository extends JpaRepository<StaffRole, java.util.UUID> {
}
