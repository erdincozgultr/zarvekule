package com.zarvekule.blog.service;

import com.zarvekule.blog.dto.BlogEntryRequest;
import com.zarvekule.blog.dto.BlogEntryResponse;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BlogEntryService {

    BlogEntryResponse create(String authenticatedUsername, BlogEntryRequest request);

    BlogEntryResponse update(String authenticatedUsername, Long id, BlogEntryRequest request);

    void delete(String authenticatedUsername, Long id);

    Page<BlogEntrySummary> getPublishedBlogs(Pageable pageable);

    BlogEntryResponse getBySlug(String slug);

    Page<BlogEntrySummary> getMyBlogs(String authenticatedUsername, Pageable pageable);

    void increaseViewCount(Long id);

    BlogEntryResponse updateStatus(String authenticatedUsername, Long id, BlogStatus newStatus);

    // Yeni arama metodu
    List<BlogEntrySummary> searchBlogs(String query);
}