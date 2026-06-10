package com.phannhubao.repository;

import com.phannhubao.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUserIdOrderByCreatedAtAsc(Long userId);

    Optional<CartItem> findByUserIdAndProductIdAndSelectedSizeAndSelectedColor(
            Long userId,
            UUID productId,
            String selectedSize,
            String selectedColor
    );

    Optional<CartItem> findByIdAndUserId(UUID id, Long userId);

    void deleteByUserId(Long userId);
}
