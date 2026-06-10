package com.phannhubao.service;

import com.phannhubao.dto.request.TagRequest;
import com.phannhubao.dto.response.TagResponse;
import com.phannhubao.entity.Tag;
import com.phannhubao.exception.AppException;
import com.phannhubao.repository.ProductRepository;
import com.phannhubao.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags() {
        return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "tagName"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TagResponse getTag(UUID id) {
        return toResponse(findTag(id));
    }

    @Transactional
    public TagResponse createTag(TagRequest request) {
        String name = normalizeName(request.getTagName());
        ensureUniqueName(name, null);
        Tag tag = Tag.builder()
                .tagName(name)
                .icon(trimToNull(request.getIcon()))
                .build();
        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public TagResponse updateTag(UUID id, TagRequest request) {
        Tag tag = findTag(id);
        String name = normalizeName(request.getTagName());
        ensureUniqueName(name, id);
        tag.setTagName(name);
        tag.setIcon(trimToNull(request.getIcon()));
        return toResponse(tagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(UUID id) {
        Tag tag = findTag(id);
        if (productRepository.existsByTags_Id(id)) {
            throw new AppException("Cannot delete a tag that is used by products");
        }
        tagRepository.delete(tag);
    }

    private Tag findTag(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new AppException("Tag not found"));
    }

    private void ensureUniqueName(String name, UUID tagId) {
        tagRepository.findByTagNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(tagId)) {
                throw new AppException("Tag name already exists");
            }
        });
    }

    private String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim().toUpperCase();
        if (normalized.isEmpty()) {
            throw new AppException("Tag name is required");
        }
        return normalized;
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private TagResponse toResponse(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .tagName(tag.getTagName())
                .icon(tag.getIcon())
                .createdAt(tag.getCreatedAt())
                .updatedAt(tag.getUpdatedAt())
                .build();
    }
}
