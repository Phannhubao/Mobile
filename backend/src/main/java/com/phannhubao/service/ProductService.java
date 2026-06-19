package com.phannhubao.service;

import com.phannhubao.dto.request.ProductCreateRequest;
import com.phannhubao.dto.request.ProductUpdateRequest;
import com.phannhubao.entity.Category;
import com.phannhubao.entity.Product;
import com.phannhubao.entity.Tag;
import com.phannhubao.repository.CategoryRepository;
import com.phannhubao.repository.ProductRepository;
import com.phannhubao.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getSaleProducts() {
        return productRepository.findByTagsTagNameIgnoreCase("SALE");
    }

    public List<Product> getNewProducts() {
        return productRepository.findByTagsTagNameIgnoreCase("NEW");
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByTag(String tagName) {
        return productRepository.findByTagsTagNameIgnoreCase(tagName);
    }

    @Transactional
    public Product createProduct(ProductCreateRequest request) {
        if (productRepository.findByProductName(request.getProductName()).isPresent()) {
            throw new RuntimeException("Product with name '" + request.getProductName() + "' already exists!");
        }

        String slug = request.getProductName().toLowerCase()
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-");

        Product product = Product.builder()
                .productName(request.getProductName())
                .brandName(request.getBrandName())
                .slug(slug)
                .imageUrl(request.getImageUrl())
                .salePrice(request.getSalePrice() != null ? request.getSalePrice() : 0.0)
                .comparePrice(request.getComparePrice())
                .quantity(request.getQuantity())
                .shortDescription(request.getShortDescription() != null ? request.getShortDescription() : "")
                .productDescription(request.getProductDescription() != null ? request.getProductDescription() : "")
                .productType(request.getProductType() != null ? request.getProductType() : "simple")
                .published(true)
                .ratingAverage(0.0)
                .reviewCount(0)
                .sizes(request.getSizes() != null ? request.getSizes() : new HashSet<>())
                .colors(request.getColors() != null ? request.getColors() : new HashSet<>())
                .build();

        if (request.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByTagNameIgnoreCase(tagName)
                        .orElseGet(() -> tagRepository.save(Tag.builder().tagName(tagName.toUpperCase()).build()));
                tags.add(tag);
            }
            product.setTags(tags);
        }

        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>();
            for (String catIdStr : request.getCategoryIds()) {
                UUID catId = UUID.fromString(catIdStr);
                categoryRepository.findById(catId).ifPresent(categories::add);
            }
            product.setCategories(categories);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(UUID id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        if (request.getProductName() != null) {
            if (!product.getProductName().equalsIgnoreCase(request.getProductName()) &&
                    productRepository.findByProductName(request.getProductName()).isPresent()) {
                throw new RuntimeException("Product with name '" + request.getProductName() + "' already exists!");
            }
            product.setProductName(request.getProductName());
            product.setSlug(request.getProductName().toLowerCase()
                    .replaceAll("[^a-z0-9\\s]", "")
                    .replaceAll("\\s+", "-"));
        }
        if (request.getBrandName() != null) {
            product.setBrandName(request.getBrandName());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getSalePrice() != null) {
            product.setSalePrice(request.getSalePrice());
        }
        if (request.getComparePrice() != null) {
            product.setComparePrice(request.getComparePrice());
        }
        if (request.getShortDescription() != null) {
            product.setShortDescription(request.getShortDescription());
        }
        if (request.getProductDescription() != null) {
            product.setProductDescription(request.getProductDescription());
        }
        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (request.getProductType() != null) {
            product.setProductType(request.getProductType());
        }
        if (request.getSizes() != null) {
            product.getSizes().clear();
            product.getSizes().addAll(request.getSizes());
        }
        if (request.getColors() != null) {
            product.getColors().clear();
            product.getColors().addAll(request.getColors());
        }

        if (request.getTags() != null) {
            Set<Tag> updatedTags = new HashSet<>();
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByTagNameIgnoreCase(tagName)
                        .orElseGet(() -> {
                            Tag newTag = Tag.builder().tagName(tagName.toUpperCase()).build();
                            return tagRepository.save(newTag);
                        });
                updatedTags.add(tag);
            }
            product.setTags(updatedTags);
        }

        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>();
            for (String catIdStr : request.getCategoryIds()) {
                UUID catId = UUID.fromString(catIdStr);
                categoryRepository.findById(catId).ifPresent(categories::add);
            }
            product.setCategories(categories);
        }

        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        productRepository.delete(product);
    }

    public List<Product> getProductsByCategory(UUID categoryId) {
        List<UUID> ids = new ArrayList<>();
        ids.add(categoryId);
        List<Category> subCategories = categoryRepository.findByParentId(categoryId);
        for (Category sub : subCategories) {
            ids.add(sub.getId());
        }
        return productRepository.findByCategoryIds(ids);
    }
}
