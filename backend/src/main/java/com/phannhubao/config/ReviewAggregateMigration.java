package com.phannhubao.config;

import com.phannhubao.entity.Product;
import com.phannhubao.entity.Review;
import com.phannhubao.repository.ProductRepository;
import com.phannhubao.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(2)
@RequiredArgsConstructor
@Slf4j
public class ReviewAggregateMigration implements CommandLineRunner {
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public void run(String... args) {
        List<Product> products = productRepository.findAll();
        boolean needsUpdate = products.stream().anyMatch(this::hasStaleReviewAggregate);
        if (!needsUpdate) {
            log.info("Review aggregates already up-to-date.");
            return;
        }
        for (Product product : products) {
            List<Review> reviews = reviewRepository.findByProductId(product.getId());
            product.setReviewCount(reviews.size());
            product.setRatingAverage(reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0));
        }
        productRepository.saveAll(products);
        log.info("Synchronized review aggregates for {} products.", products.size());
    }

    private boolean hasStaleReviewAggregate(Product product) {
        Integer reviewCount = product.getReviewCount();
        Double ratingAverage = product.getRatingAverage();
        return reviewCount == null ||
                (reviewCount == 0 && ratingAverage != null && ratingAverage > 0.0);
    }
}
