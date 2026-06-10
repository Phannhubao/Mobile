package com.phannhubao.repository;

import com.phannhubao.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.phannhubao.entity.WishlistId;
import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, WishlistId> {
    List<Wishlist> findByUserId(Long userId);
    boolean existsByUserIdAndProductId(Long userId, UUID productId);
}
