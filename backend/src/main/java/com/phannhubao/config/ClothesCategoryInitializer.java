package com.phannhubao.config;

import com.phannhubao.entity.Category;
import com.phannhubao.entity.Product;
import com.phannhubao.repository.CategoryRepository;
import com.phannhubao.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
@Order(3)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class ClothesCategoryInitializer implements CommandLineRunner {

    private static final Map<String, String> CLOTHES_CATEGORIES = new LinkedHashMap<>();

    static {
        CLOTHES_CATEGORIES.put("Tops", "Tops and upper-body clothing");
        CLOTHES_CATEGORIES.put("Shirts & Blouses", "Shirts and blouses");
        CLOTHES_CATEGORIES.put("Cardigans & Sweaters", "Cardigans and sweaters");
        CLOTHES_CATEGORIES.put("Knitwear", "Knitted clothing");
        CLOTHES_CATEGORIES.put("Blazers", "Blazers and tailored jackets");
        CLOTHES_CATEGORIES.put("Outerwear", "Jackets, coats and parkas");
        CLOTHES_CATEGORIES.put("Pants", "Pants and trousers");
        CLOTHES_CATEGORIES.put("Jeans", "Denim jeans");
        CLOTHES_CATEGORIES.put("Shorts", "Shorts");
        CLOTHES_CATEGORIES.put("Skirts", "Skirts");
        CLOTHES_CATEGORIES.put("Dresses", "Dresses");
    }

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Category clothes = categoryRepository.findByCategoryNameIgnoreCase("Clothes")
                .orElseGet(() -> categoryRepository.save(Category.builder()
                        .categoryName("Clothes")
                        .categoryDescription("Clothes collection")
                        .image("/images/cat_clothes.jpg")
                        .active(true)
                        .build()));

        Map<String, Category> children = new LinkedHashMap<>();
        CLOTHES_CATEGORIES.forEach((name, description) -> {
            Category child = categoryRepository.findByCategoryNameIgnoreCase(name)
                    .orElseGet(() -> Category.builder().categoryName(name).build());
            child.setParent(clothes);
            child.setActive(true);
            if (child.getCategoryDescription() == null || child.getCategoryDescription().isBlank()) {
                child.setCategoryDescription(description);
            }
            children.put(name, categoryRepository.save(child));
        });

        for (Product product : productRepository.findAll()) {
            Set<Category> categories = product.getCategories();
            boolean changed = matchingChildNames(product.getProductName()).stream()
                    .map(children::get)
                    .filter(category -> category != null && categories.stream()
                            .noneMatch(existing -> existing.getId().equals(category.getId())))
                    .map(categories::add)
                    .reduce(false, Boolean::logicalOr);
            if (changed) {
                productRepository.save(product);
            }
        }
    }

    private Set<String> matchingChildNames(String productName) {
        String name = productName == null ? "" : productName.toLowerCase(Locale.ROOT);
        java.util.LinkedHashSet<String> matches = new java.util.LinkedHashSet<>();

        if (containsAny(name, "shirt", "blouse", "hoodie", "crop top", "active wear")) {
            matches.add("Tops");
        }
        if (containsAny(name, "shirt", "blouse")) {
            matches.add("Shirts & Blouses");
        }
        if (containsAny(name, "cardigan", "sweater", "pullover")) {
            matches.add("Cardigans & Sweaters");
            matches.add("Knitwear");
        }
        if (containsAny(name, "suit", "blazer")) {
            matches.add("Blazers");
        }
        if (containsAny(name, "jacket", "parka", "coat")) {
            matches.add("Outerwear");
        }
        if (containsAny(name, "pants", "trousers", "jeans")) {
            matches.add("Pants");
        }
        if (name.contains("jeans")) {
            matches.add("Jeans");
        }
        if (name.contains("shorts")) {
            matches.add("Shorts");
        }
        if (name.contains("skirt")) {
            matches.add("Skirts");
        }
        if (name.contains("dress")) {
            matches.add("Dresses");
        }
        return matches;
    }

    private boolean containsAny(String value, String... terms) {
        for (String term : terms) {
            if (value.contains(term)) {
                return true;
            }
        }
        return false;
    }
}
