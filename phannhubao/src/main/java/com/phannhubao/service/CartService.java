package com.phannhubao.service;

import com.phannhubao.dto.request.CartItemCreateRequest;
import com.phannhubao.dto.response.CartItemResponse;
import com.phannhubao.entity.CartItem;
import com.phannhubao.entity.Product;
import com.phannhubao.entity.User;
import com.phannhubao.exception.AppException;
import com.phannhubao.repository.CartItemRepository;
import com.phannhubao.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        return cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CartItemResponse addItem(User user, CartItemCreateRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException("Product not found"));
        String selectedSize = normalizeVariant(request.getSelectedSize());
        String selectedColor = normalizeVariant(request.getSelectedColor());
        int quantityToAdd = request.getQuantity() == null ? 1 : request.getQuantity();

        CartItem item = cartItemRepository
                .findByUserIdAndProductIdAndSelectedSizeAndSelectedColor(
                        user.getId(), product.getId(), selectedSize, selectedColor
                )
                .orElseGet(() -> CartItem.builder()
                        .user(user)
                        .product(product)
                        .selectedSize(selectedSize)
                        .selectedColor(selectedColor)
                        .quantity(0)
                        .build());

        item.setQuantity(item.getQuantity() + quantityToAdd);
        validateQuantity(product, item.getQuantity());
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public CartItemResponse updateQuantity(Long userId, UUID itemId, int quantity) {
        CartItem item = getOwnedItem(userId, itemId);
        validateQuantity(item.getProduct(), quantity);
        item.setQuantity(quantity);
        return toResponse(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(Long userId, UUID itemId) {
        cartItemRepository.delete(getOwnedItem(userId, itemId));
    }

    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItem getOwnedItem(Long userId, UUID itemId) {
        return cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new AppException("Cart item not found"));
    }

    private void validateQuantity(Product product, int quantity) {
        if (quantity < 1) {
            throw new AppException("Quantity must be at least 1");
        }
        if (product.isDisableOutOfStock() && quantity > product.getQuantity()) {
            throw new AppException("Requested quantity exceeds available stock");
        }
    }

    private String normalizeVariant(String value) {
        return value == null ? "" : value.trim();
    }

    private CartItemResponse toResponse(CartItem item) {
        return CartItemResponse.builder()
                .id(item.getId())
                .product(item.getProduct())
                .selectedSize(item.getSelectedSize())
                .selectedColor(item.getSelectedColor())
                .quantity(item.getQuantity())
                .build();
    }
}
