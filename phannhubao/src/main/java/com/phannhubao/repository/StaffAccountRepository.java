package com.phannhubao.repository;

import com.phannhubao.entity.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAccountRepository extends JpaRepository<StaffAccount, java.util.UUID> {
}
