package com.phannhubao.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String slug;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    private String sku;

    @Column(name = "sale_price", nullable = false)
    private double salePrice;

    @Column(name = "compare_price")
    private Double comparePrice;

    @Column(name = "buying_price")
    private Double buyingPrice;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "short_description", nullable = false, length = 165)
    private String shortDescription;

    @Column(name = "product_description", nullable = false, columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "product_type", length = 64)
    private String productType;

    @Builder.Default
    private boolean published = false;

    @Column(name = "disable_out_of_stock")
    @Builder.Default
    private boolean disableOutOfStock = true;

    @Column(name = "rating_average")
    private Double ratingAverage;

    @Column(name = "review_count")
    private Integer reviewCount;

    @Column(columnDefinition = "TEXT")
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private StaffAccount createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private StaffAccount updatedBy;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_tags",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_sizes", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "size")
    @Builder.Default
    private Set<String> sizes = new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_colors", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "color")
    @Builder.Default
    private Set<String> colors = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Gallery> gallery = new HashSet<>();

    @Transient
    private Set<UUID> categoryIds;

    public Set<UUID> getCategoryIds() {
        if (categories == null) return new HashSet<>();
        return categories.stream().map(Category::getId).collect(java.util.stream.Collectors.toSet());
    }

    public void setCategoryIds(Set<UUID> categoryIds) {
        this.categoryIds = categoryIds;
    }
}
