package com.zarvekule.homebrew.service;

import com.zarvekule.homebrew.dto.HomebrewEntryPatchRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.enums.HomebrewCategory;

import java.util.List;

public interface HomebrewEntryService {

    HomebrewEntryResponse create(String authenticatedUsername, HomebrewEntryRequest request);

    HomebrewEntryResponse update(String authenticatedUsername, Long id, HomebrewEntryPatchRequest request);

    void delete(String authenticatedUsername, Long id);

    // Public listeleme metodlarına username eklendi
    List<HomebrewEntryResponse> getPublishedHomebrews(String authenticatedUsername);

    List<HomebrewEntryResponse> getPublishedHomebrewsByCategory(HomebrewCategory category, String authenticatedUsername);

    List<HomebrewEntryResponse> search(String keyword, String authenticatedUsername);

    HomebrewEntryResponse getBySlug(String slug, String authenticatedUsername);

    List<HomebrewEntryResponse> getMyHomebrews(String authenticatedUsername);

    void increaseViewCount(Long id);

//    HomebrewEntryResponse forkEntry(String username, Long entryId);

    long countPublishedHomebrewsByCategory(HomebrewCategory category);
}