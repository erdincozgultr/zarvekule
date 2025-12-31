package com.zarvekule.homebrew.service;

import com.zarvekule.homebrew.dto.HomebrewEntryPatchRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.dto.HomebrewEntryListResponse;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface HomebrewEntryService {

    // ============================================
    // CRUD İŞLEMLERİ
    // ============================================

    HomebrewEntryResponse create(String authenticatedUsername, HomebrewEntryRequest request);

    HomebrewEntryResponse update(String authenticatedUsername, Long id, HomebrewEntryPatchRequest request);

    void delete(String authenticatedUsername, Long id);

    // ============================================
    // PAGINATION DESTEKLİ LİSTELEME (Yeni!)
    // ============================================

    /**
     * Tüm yayınlanmış homebrew'lar (sayfalama ile)
     */
    Page<HomebrewEntryListResponse> getPublishedHomebrews(Pageable pageable, String authenticatedUsername);

    /**
     * Kategoriye göre homebrew'lar (sayfalama ile)
     */
    Page<HomebrewEntryListResponse> getPublishedHomebrewsByCategory(
            HomebrewCategory category,
            Pageable pageable,
            String authenticatedUsername
    );

    /**
     * Arama (sayfalama ile)
     */
    Page<HomebrewEntryListResponse> search(
            String keyword,
            Pageable pageable,
            String authenticatedUsername
    );

    /**
     * Kategori bazlı arama (sayfalama ile)
     */
    Page<HomebrewEntryListResponse> searchByCategory(
            HomebrewCategory category,
            String keyword,
            Pageable pageable,
            String authenticatedUsername
    );

    // ============================================
    // DETAY & VIEW İŞLEMLERİ
    // ============================================

    HomebrewEntryResponse getBySlug(String slug, String authenticatedUsername);

    void increaseViewCount(Long id);

    // ============================================
    // KULLANICI İŞLEMLERİ
    // ============================================

    /**
     * Kullanıcının kendi homebrew'ları (tüm statuslar)
     */
    List<HomebrewEntryResponse> getMyHomebrews(String authenticatedUsername);

    /**
     * Belirli kullanıcının yayınlanmış homebrew'ları (public)
     */
    List<HomebrewEntryResponse> getUserHomebrews(String username, String authenticatedUsername);

    // ============================================
    // İSTATİSTİKLER
    // ============================================

    /**
     * Tüm kategori sayıları
     */
    Map<HomebrewCategory, Long> getCategoryCounts();

    /**
     * Belirli kategori sayısı
     */
    long countPublishedHomebrewsByCategory(HomebrewCategory category);

    // ============================================
    // GELECEK ÖZELLİKLER
    // ============================================

//    HomebrewEntryResponse forkEntry(String username, Long entryId);
}