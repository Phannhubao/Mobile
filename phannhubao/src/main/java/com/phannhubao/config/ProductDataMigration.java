package com.phannhubao.config;

import com.phannhubao.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
@RequiredArgsConstructor
@Slf4j
public class ProductDataMigration implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        int normalizedProducts = productRepository.normalizeNullBooleanValues();
        if (normalizedProducts > 0) {
            log.info("Normalized nullable boolean values for {} existing products.", normalizedProducts);
        }
    }
}
