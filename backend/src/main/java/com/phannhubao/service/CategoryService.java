package com.phannhubao.service;

import com.phannhubao.dto.request.CategoryRequest;
import com.phannhubao.dto.response.CategoryResponse;
import com.phannhubao.entity.Category;
import com.phannhubao.exception.AppException;
import com.phannhubao.repository.CategoryRepository;
import com.phannhubao.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "categoryName"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(UUID id) {
        return toResponse(findCategory(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String name = normalizeName(request.getCategoryName());
        ensureUniqueName(name, null);

        Category category = Category.builder()
                .categoryName(name)
                .parent(resolveParent(request.getParentId(), null))
                .categoryDescription(trimToNull(request.getCategoryDescription()))
                .icon(trimToNull(request.getIcon()))
                .image(trimToNull(request.getImage()))
                .placeholder(trimToNull(request.getPlaceholder()))
                .active(request.getActive() == null || request.getActive())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Category category = findCategory(id);
        String name = normalizeName(request.getCategoryName());
        ensureUniqueName(name, id);

        category.setCategoryName(name);
        category.setParent(resolveParent(request.getParentId(), id));
        category.setCategoryDescription(trimToNull(request.getCategoryDescription()));
        category.setIcon(trimToNull(request.getIcon()));
        category.setImage(trimToNull(request.getImage()));
        category.setPlaceholder(trimToNull(request.getPlaceholder()));
        category.setActive(request.getActive() == null || request.getActive());

        return toResponse(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = findCategory(id);
        if (categoryRepository.existsByParent_Id(id)) {
            throw new AppException("Cannot delete a category that has child categories");
        }
        if (productRepository.existsByCategories_Id(id)) {
            throw new AppException("Cannot delete a category that is used by products");
        }
        categoryRepository.delete(category);
    }

    private Category findCategory(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException("Category not found"));
    }

    private Category resolveParent(UUID parentId, UUID categoryId) {
        if (parentId == null) {
            return null;
        }
        if (parentId.equals(categoryId)) {
            throw new AppException("A category cannot be its own parent");
        }
        Category parent = findCategory(parentId);
        Category ancestor = parent;
        while (ancestor != null) {
            if (ancestor.getId().equals(categoryId)) {
                throw new AppException("Category parent selection would create a cycle");
            }
            ancestor = ancestor.getParent();
        }
        return parent;
    }

    private void ensureUniqueName(String name, UUID categoryId) {
        categoryRepository.findByCategoryNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(categoryId)) {
                throw new AppException("Category name already exists");
            }
        });
    }

    private String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.isEmpty()) {
            throw new AppException("Category name is required");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .parentId(category.getParent() == null ? null : category.getParent().getId())
                .categoryName(category.getCategoryName())
                .categoryDescription(category.getCategoryDescription())
                .icon(category.getIcon())
                .image(category.getImage())
                .placeholder(category.getPlaceholder())
                .active(category.getActive())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
