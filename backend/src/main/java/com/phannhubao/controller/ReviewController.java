package com.phannhubao.controller;

import com.phannhubao.dto.request.ReviewRequest;
import com.phannhubao.dto.response.RatingSummaryResponse;
import com.phannhubao.dto.response.ReviewResponse;
import com.phannhubao.entity.Review;
import com.phannhubao.security.CustomUserDetails;
import com.phannhubao.service.ReviewService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/product/{productId}/summary")
    public ResponseEntity<RatingSummaryResponse> getProductRatingSummary(
            @PathVariable UUID productId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails != null ? userDetails.getId() : null;
        return ResponseEntity.ok(reviewService.getRatingSummaryForProduct(productId, userId));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getReviewsForProduct(productId));
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<?> createProductReview(
            @PathVariable UUID productId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReviewRequest request
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            return ResponseEntity.ok(reviewService.createReview(productId, userDetails.getUser(), request));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(java.util.Map.of("message", e.getMessage()));
        }
    }
}
