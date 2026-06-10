package com.phannhubao.service;

import com.phannhubao.dto.request.ReviewRequest;
import com.phannhubao.dto.response.RatingSummaryResponse;
import com.phannhubao.dto.response.ReviewResponse;
import com.phannhubao.entity.Product;
import com.phannhubao.entity.Review;
import com.phannhubao.entity.User;
import com.phannhubao.repository.ProductRepository;
import com.phannhubao.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public RatingSummaryResponse getRatingSummaryForProduct(UUID productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        
        int totalRatings = reviews.size();
        if (totalRatings == 0) {
            Map<Integer, Integer> emptyCounts = new HashMap<>();
            for (int i = 1; i <= 5; i++) emptyCounts.put(i, 0);
            return RatingSummaryResponse.builder()
                    .averageRating(0.0)
                    .totalRatings(0)
                    .ratingsCount(emptyCounts)
                    .build();
        }

        double sum = 0;
        Map<Integer, Integer> counts = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            counts.put(i, 0);
        }

        for (Review review : reviews) {
            int r = review.getRating();
            sum += r;
            counts.put(r, counts.getOrDefault(r, 0) + 1);
        }

        double average = sum / totalRatings;
        // Round to 1 decimal place
        average = Math.round(average * 10.0) / 10.0;

        return RatingSummaryResponse.builder()
                .averageRating(average)
                .totalRatings(totalRatings)
                .ratingsCount(counts)
                .build();
    }

    public List<ReviewResponse> getReviewsForProduct(UUID productId) {
        List<Review> reviews = reviewRepository.findByProductId(productId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", java.util.Locale.ENGLISH);
        
        return reviews.stream().map(review -> {
            String name = review.getUser().getName();
            String avatar = (name != null && !name.isEmpty()) ? name.substring(0, 1).toUpperCase() : "U";
            String dateStr = review.getCreatedAt() != null ? review.getCreatedAt().format(formatter) : "Today";
            
            return ReviewResponse.builder()
                    .id(review.getId())
                    .name(name)
                    .avatar(avatar)
                    .avatarUrl(review.getUser().getAvatarUrl())
                    .rating(review.getRating())
                    .date(dateStr)
                    .content(review.getContent())
                    .helpful(false)
                    .hasPhoto(review.getPhotos() != null && !review.getPhotos().isEmpty())
                    .photos(review.getPhotos() != null ? review.getPhotos() : new ArrayList<>())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public Review createReview(UUID productId, User user, ReviewRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
        
        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .content(request.getContent())
                .title(request.getTitle())
                .photos(request.getPhotos() != null ? request.getPhotos() : new ArrayList<>())
                .createdAt(java.time.OffsetDateTime.now())
                .updatedAt(java.time.OffsetDateTime.now())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        List<Review> productReviews = reviewRepository.findByProductId(productId);
        product.setReviewCount(productReviews.size());
        product.setRatingAverage(productReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0));
        productRepository.save(product);
        return savedReview;
    }
}
