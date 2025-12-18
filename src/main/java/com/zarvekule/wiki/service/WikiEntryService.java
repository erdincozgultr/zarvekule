package com.zarvekule.wiki.service;

import com.zarvekule.wiki.dto.WikiEntryRequest;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.enums.ContentCategory;

import java.util.List;

public interface WikiEntryService {
    WikiEntryResponse create(String authenticatedUsername, WikiEntryRequest request);

    WikiEntryResponse update(Long id, WikiEntryRequest request, String authenticatedUsername);

    void delete(Long id, String authenticatedUsername);

    // Listeleme metodlarına username eklendi
    WikiEntryResponse getBySlug(String slug, String authenticatedUsername);

    List<WikiEntryResponse> getPublishedEntries(String authenticatedUsername);

    List<WikiEntryResponse> getPublishedEntriesByCategory(ContentCategory category, String authenticatedUsername);

    List<WikiEntryResponse> search(String keyword, String authenticatedUsername);
}