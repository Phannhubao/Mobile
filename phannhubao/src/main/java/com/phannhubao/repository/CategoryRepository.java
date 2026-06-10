package com.phannhubao.repository;

import com.phannhubao.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, java.util.UUID> {
    java.util.Optional<Category> findByCategoryNameIgnoreCase(String categoryName);
    boolean existsByParent_Id(java.util.UUID parentId);
}
