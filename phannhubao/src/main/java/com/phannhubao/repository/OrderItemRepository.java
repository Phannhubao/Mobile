package com.phannhubao.repository;

import com.phannhubao.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, java.util.UUID> {
    java.util.List<OrderItem> findByOrderId(String orderId);
}
