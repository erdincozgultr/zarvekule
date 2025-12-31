package com.zarvekule.homebrew.controller;

import com.zarvekule.homebrew.dto.HomebrewEntryPatchRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.dto.HomebrewEntryListResponse;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.service.HomebrewEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/homebrews")
@RequiredArgsConstructor
public class HomebrewEntryController {

    private final HomebrewEntryService homebrewService;

    // ============================================
    // PUBLIC LİSTELEME ENDPOINTLERİ (PAGINATION)
    // ============================================

    /**
     * Tüm yayınlanmış homebrew'lar (sayfalama ile)
     * GET /api/homebrews?page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Page<HomebrewEntryListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getPublishedHomebrews(pageable, username));
    }

    /**
     * Kategoriye göre homebrew'lar (sayfalama ile)
     * GET /api/homebrews/category/SPELL?page=0&size=20
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<HomebrewEntryListResponse>> getByCategory(
            @PathVariable HomebrewCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getPublishedHomebrewsByCategory(category, pageable, username));
    }

    /**
     * Arama (sayfalama ile)
     * GET /api/homebrews/search?q=dragon&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<Page<HomebrewEntryListResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.search(q, pageable, username));
    }

    /**
     * Kategori bazlı arama (sayfalama ile)
     * GET /api/homebrews/category/SPELL/search?q=fire&page=0&size=20
     */
    @GetMapping("/category/{category}/search")
    public ResponseEntity<Page<HomebrewEntryListResponse>> searchByCategory(
            @PathVariable HomebrewCategory category,
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.searchByCategory(category, q, pageable, username));
    }

    // ============================================
    // DETAY ENDPOINTLERİ
    // ============================================

    /**
     * Slug ile homebrew detay
     * GET /api/homebrews/slug/my-custom-spell
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<HomebrewEntryResponse> getBySlug(
            @PathVariable String slug,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getBySlug(slug, username));
    }

    /**
     * ID ile homebrew detay (geriye dönük uyumluluk)
     * GET /api/homebrews/read/{slug}
     */
    @GetMapping("/read/{slug}")
    public ResponseEntity<HomebrewEntryResponse> getBySlugLegacy(
            @PathVariable String slug,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getBySlug(slug, username));
    }

    // ============================================
    // İSTATİSTİK ENDPOINTLERİ
    // ============================================

    /**
     * Kategori sayıları (Wiki ile uyumlu)
     * GET /api/homebrews/stats/counts
     */
    @GetMapping("/stats/counts")
    public ResponseEntity<java.util.Map<HomebrewCategory, Long>> getCategoryCounts() {
        return ResponseEntity.ok(homebrewService.getCategoryCounts());
    }

    /**
     * Belirli kategori sayısı
     * GET /api/homebrews/stats/count/SPELL
     */
    @GetMapping("/stats/count/{category}")
    public ResponseEntity<Long> countByCategory(@PathVariable HomebrewCategory category) {
        return ResponseEntity.ok(homebrewService.countPublishedHomebrewsByCategory(category));
    }

    // ============================================
    // KULLANICI İŞLEMLERİ
    // ============================================

    /**
     * Kullanıcının kendi homebrew'ları
     * GET /api/homebrews/my-homebrews
     */
    @GetMapping("/my-homebrews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<HomebrewEntryResponse>> getMyHomebrews(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(homebrewService.getMyHomebrews(userDetails.getUsername()));
    }

    /**
     * Belirli kullanıcının homebrew'ları (public)
     * GET /api/homebrews/user/{username}
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<List<HomebrewEntryResponse>> getUserHomebrews(
            @PathVariable String username,
            Principal principal) {
        String authenticatedUsername = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getUserHomebrews(username, authenticatedUsername));
    }

    // ============================================
    // CRUD İŞLEMLERİ
    // ============================================

    /**
     * Yeni homebrew oluştur
     * POST /api/homebrews
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HomebrewEntryResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody HomebrewEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(homebrewService.create(userDetails.getUsername(), request));
    }

    /**
     * Homebrew güncelle
     * PATCH /api/homebrews/{id}
     */
    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<HomebrewEntryResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody HomebrewEntryPatchRequest request) {
        return ResponseEntity.ok(homebrewService.update(userDetails.getUsername(), id, request));
    }

    /**
     * Homebrew sil
     * DELETE /api/homebrews/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        homebrewService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    // ============================================
    // GELECEK ÖZELLİKLER (Commented)
    // ============================================

//    @PostMapping("/{id}/fork")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<HomebrewEntryResponse> forkEntry(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @PathVariable Long id) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(homebrewService.forkEntry(userDetails.getUsername(), id));
//    }
}