package com.zarvekule.wiki.service;

import com.zarvekule.wiki.dto.WikiBulkImportRequest;
import com.zarvekule.wiki.dto.WikiEntryListResponse;
import com.zarvekule.wiki.dto.WikiEntryRequest;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.enums.ContentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WikiEntryService {

    // === CRUD ===

    WikiEntryResponse create(String username, WikiEntryRequest request);

    WikiEntryResponse update(Long id, WikiEntryRequest request, String username);

    void delete(Long id, String username);

    // === DETAY (tam metadata) ===

    WikiEntryResponse getById(Long id, String username);

    WikiEntryResponse getBySlug(String slug, String username);

    // === LİSTE (pagination, metadata YOK) ===

    Page<WikiEntryListResponse> getAll(Pageable pageable, String username);

    Page<WikiEntryListResponse> getByCategory(ContentCategory category, Pageable pageable, String username);

    // === ARAMA ===

    Page<WikiEntryListResponse> search(String keyword, Pageable pageable, String username);

    Page<WikiEntryListResponse> searchByCategory(ContentCategory category, String keyword, Pageable pageable, String username);

    Page<WikiEntryListResponse> searchTurkish(String keyword, Pageable pageable, String username);

    // === İSTATİSTİK ===

    Map<ContentCategory, Long> getCategoryCounts();

    long getCategoryCount(ContentCategory category);

    // === IMPORT ===

    WikiEntryResponse importEntry(WikiBulkImportRequest request, String username);

    int bulkImport(List<WikiBulkImportRequest> requests, String username);
}
