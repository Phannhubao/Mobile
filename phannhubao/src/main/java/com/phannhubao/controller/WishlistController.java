package com.phannhubao.controller;

import com.phannhubao.entity.Product;
import com.phannhubao.security.CustomUserDetails;
import com.phannhubao.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<Product>> getUserWishlist(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(wishlistService.getUserWishlistProducts(userDetails.getUser().getId()));
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> addToWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID productId) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            wishlistService.addProductToWishlist(userDetails.getUser().getId(), productId);
            return ResponseEntity.ok("Đã thêm vào danh sách yêu thích");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeFromWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID productId) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        wishlistService.removeProductFromWishlist(userDetails.getUser().getId(), productId);
        return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
    }

    @GetMapping("/{productId}/check")
    public ResponseEntity<Boolean> checkWishlistStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID productId) {
        if (userDetails == null) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(wishlistService.isProductInWishlist(userDetails.getUser().getId(), productId));
    }
}
