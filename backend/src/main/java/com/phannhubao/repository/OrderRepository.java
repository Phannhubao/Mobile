package com.phannhubao.repository;

import com.phannhubao.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    long countByCustomerUserId(Long userId);
    java.util.List<Order> findByCustomerUserId(Long userId);
}
