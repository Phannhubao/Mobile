package com.phannhubao.repository;

import com.phannhubao.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, java.util.UUID> {
    long countByCustomerUserId(Long userId);
    java.util.List<Card> findByCustomerUserId(Long userId);
}
