package com.phannhubao.repository;

import com.phannhubao.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, java.util.UUID> {
    java.util.Optional<Customer> findByUserId(Long userId);
}
