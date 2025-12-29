package com.zarvekule.wiki.controller;

import com.zarvekule.wiki.dto.WikiBulkImportRequest;
import com.zarvekule.wiki.dto.WikiEntryListResponse;
import com.zarvekule.wiki.dto.WikiEntryRequest;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.service.WikiEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wiki")
@RequiredArgsConstructor
public class WikiEntryController {

    private final WikiEntryService wikiService;

    // =====================================================
    // LİSTE ENDPOINTLERİ (PAGINATION)
    // Response: WikiEntryListResponse (metadata YOK)
    // =====================================================

    /**
     * Tüm içerikler - sayfalı
     * GET /api/wiki?page=0&size=20&sort=title,asc
     */
    @GetMapping
    public ResponseEntity<Page<WikiEntryListResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Principal principal) {

        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        String username = principal != null ? principal.getName() : null;

        return ResponseEntity.ok(wikiService.getAll(pageable, username));
    }

    /**
     * Kategori bazlı - sayfalı
     * GET /api/wiki/category/SPELLS?page=0&size=20
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<WikiEntryListResponse>> getByCategory(
            @PathVariable ContentCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            Principal principal) {

        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        String username = principal != null ? principal.getName() : null;

        return ResponseEntity.ok(wikiService.getByCategory(category, pageable, username));
    }

    // =====================================================
    // DETAY ENDPOINTLERİ
    // Response: WikiEntryResponse (metadata DAHİL)
    // =====================================================

    /**
     * Slug ile detay
     * GET /api/wiki/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<WikiEntryResponse> getBySlug(
            @PathVariable String slug,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(wikiService.getBySlug(slug, username));
    }

    /**
     * ID ile detay
     * GET /api/wiki/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<WikiEntryResponse> getById(
            @PathVariable Long id,
            Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(wikiService.getById(id, username));
    }

    // =====================================================
    // ARAMA ENDPOINTLERİ (PAGINATION)
    // =====================================================

    /**
     * Başlık araması
     * GET /api/wiki/search?q=fireball&page=0&size=20
     */
    @GetMapping("/search")
    public ResponseEntity<Page<WikiEntryListResponse>> search(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        String username = principal != null ? principal.getName() : null;

        return ResponseEntity.ok(wikiService.search(keyword, pageable, username));
    }

    /**
     * Kategori + arama
     * GET /api/wiki/category/SPELLS/search?q=fire
     */
    @GetMapping("/category/{category}/search")
    public ResponseEntity<Page<WikiEntryListResponse>> searchByCategory(
            @PathVariable ContentCategory category,
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        String username = principal != null ? principal.getName() : null;

        return ResponseEntity.ok(wikiService.searchByCategory(category, keyword, pageable, username));
    }

    /**
     * Türkçe başlık araması
     * GET /api/wiki/search/turkish?q=ateş
     */
    @GetMapping("/search/turkish")
    public ResponseEntity<Page<WikiEntryListResponse>> searchTurkish(
            @RequestParam("q") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {

        Pageable pageable = PageRequest.of(page, size);
        String username = principal != null ? principal.getName() : null;

        return ResponseEntity.ok(wikiService.searchTurkish(keyword, pageable, username));
    }

    // =====================================================
    // İSTATİSTİK ENDPOINTLERİ
    // =====================================================

    /**
     * Kategori sayıları
     * GET /api/wiki/stats/counts
     */
    @GetMapping("/stats/counts")
    public ResponseEntity<Map<ContentCategory, Long>> getCategoryCounts() {
        return ResponseEntity.ok(wikiService.getCategoryCounts());
    }

    /**
     * Belirli kategori sayısı
     * GET /api/wiki/stats/count/SPELLS
     */
    @GetMapping("/stats/count/{category}")
    public ResponseEntity<Map<String, Object>> getCategoryCount(@PathVariable ContentCategory category) {
        return ResponseEntity.ok(Map.of(
                "category", category,
                "count", wikiService.getCategoryCount(category)
        ));
    }

    /**
     * Kategoriler listesi
     * GET /api/wiki/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getCategories() {
        List<Map<String, String>> categories = Arrays.stream(ContentCategory.values())
                .map(cat -> Map.of(
                        "value", cat.name(),
                        "label", cat.getDisplayName()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    // =====================================================
    // CRUD ENDPOINTLERİ (ADMIN/MODERATOR)
    // =====================================================

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<WikiEntryResponse> create(
            Principal principal,
            @Valid @RequestBody WikiEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wikiService.create(principal.getName(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<WikiEntryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody WikiEntryRequest request,
            Principal principal) {
        return ResponseEntity.ok(wikiService.update(id, request, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        wikiService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // IMPORT ENDPOINTLERİ (ADMIN)
    // =====================================================

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WikiEntryResponse> importEntry(
            @Valid @RequestBody WikiBulkImportRequest request,
            Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wikiService.importEntry(request, principal.getName()));
    }

    @PostMapping("/import/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bulkImport(
            @RequestBody List<WikiBulkImportRequest> requests,
            Principal principal) {
        int count = wikiService.bulkImport(requests, principal.getName());
        return ResponseEntity.ok(Map.of(
                "success", true,
                "imported", count,
                "total", requests.size()
        ));
    }

    // === YARDIMCI ===

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        return PageRequest.of(page, Math.min(size, 100), sort);
    }
}
