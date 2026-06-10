package com.phannhubao.service;

import com.phannhubao.entity.Product;
import com.phannhubao.entity.Wishlist;
import com.phannhubao.entity.WishlistId;
import com.phannhubao.repository.ProductRepository;
import com.phannhubao.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    public List<Product> getUserWishlistProducts(Long userId) {
        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);
        List<UUID> productIds = wishlists.stream()
                .map(Wishlist::getProductId)
                .collect(Collectors.toList());
        return productRepository.findAllById(productIds);
    }

    @Transactional
    public void addProductToWishlist(Long userId, UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("Sản phẩm không tồn tại");
        }
        
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return;
        }

        Wishlist wishlist = Wishlist.builder()
                .userId(userId)
                .productId(productId)
                .createdAt(OffsetDateTime.now())
                .build();

        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void removeProductFromWishlist(Long userId, UUID productId) {
        WishlistId id = new WishlistId(userId, productId);
        if (wishlistRepository.existsById(id)) {
            wishlistRepository.deleteById(id);
        }
    }
    
    public boolean isProductInWishlist(Long userId, UUID productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }
}
