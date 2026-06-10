package com.phannhubao.controller;

import com.phannhubao.dto.request.CartItemCreateRequest;
import com.phannhubao.dto.request.CartItemUpdateRequest;
import com.phannhubao.dto.response.CartItemResponse;
import com.phannhubao.security.CustomUserDetails;
import com.phannhubao.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return ResponseEntity.ok(cartService.getCart(userDetails.getUser().getId()));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CartItemCreateRequest request
    ) {
        return ResponseEntity.ok(cartService.addItem(userDetails.getUser(), request));
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID itemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        return ResponseEntity.ok(
                cartService.updateQuantity(userDetails.getUser().getId(), itemId, request.getQuantity())
        );
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID itemId
    ) {
        cartService.removeItem(userDetails.getUser().getId(), itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        cartService.clearCart(userDetails.getUser().getId());
        return ResponseEntity.noContent().build();
    }
}
