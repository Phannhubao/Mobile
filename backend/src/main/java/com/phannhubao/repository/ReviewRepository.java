package com.phannhubao.repository;

import com.phannhubao.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    long countByUserId(Long userId);
    List<Review> findByUserId(Long userId);
    List<Review> findByProductId(java.util.UUID productId);
    boolean existsByUserIdAndProductId(Long userId, java.util.UUID productId);
}
