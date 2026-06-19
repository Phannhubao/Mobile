package com.phannhubao.repository;

import com.phannhubao.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Modifying
    @Transactional
    @Query(value = """
            UPDATE products
            SET disable_out_of_stock = COALESCE(disable_out_of_stock, TRUE),
                published = COALESCE(published, FALSE)
            WHERE disable_out_of_stock IS NULL OR published IS NULL
            """, nativeQuery = true)
    int normalizeNullBooleanValues();
    
    @Query("SELECT p FROM Product p WHERE p.comparePrice IS NOT NULL AND p.comparePrice > p.salePrice")
    List<Product> findSaleProducts();
    
    List<Product> findByTagsTagNameIgnoreCase(String tagName);
    java.util.Optional<Product> findByProductName(String productName);
    java.util.Optional<Product> findBySlug(String slug);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(java.util.UUID categoryId);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id IN :categoryIds")
    List<Product> findByCategoryIds(@Param("categoryIds") java.util.Collection<java.util.UUID> categoryIds);

    boolean existsByCategories_Id(UUID categoryId);
    boolean existsByTags_Id(UUID tagId);
}
