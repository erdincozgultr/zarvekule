package com.zarvekule.wiki.controller;

import com.zarvekule.wiki.dto.WikiEntryRequest;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.service.WikiEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/wiki")
@RequiredArgsConstructor
public class WikiEntryController {

    private final WikiEntryService wikiService;

    // Public listeleme (Username opsiyonel)
    @GetMapping("/public")
    public ResponseEntity<List<WikiEntryResponse>> getPublishedEntries(Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(wikiService.getPublishedEntries(username));
    }

    // Detay Görüntüleme (Username opsiyonel)
    @GetMapping("/read/{slug}")
    public ResponseEntity<WikiEntryResponse> getBySlug(@PathVariable String slug, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(wikiService.getBySlug(slug, username));
    }

    // Kategoriye Göre (Username opsiyonel)
    @GetMapping("/category/{category}")
    public ResponseEntity<List<WikiEntryResponse>> getByCategory(@PathVariable ContentCategory category, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(wikiService.getPublishedEntriesByCategory(category, username));
    }

    // Arama (Username opsiyonel)
    @GetMapping("/search")
    public ResponseEntity<List<WikiEntryResponse>> search(@RequestParam String q, Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(wikiService.search(q, username));
    }

    // --- YAZMA İŞLEMLERİ ---

    @PostMapping
    public ResponseEntity<WikiEntryResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WikiEntryRequest request) {
        return ResponseEntity.ok(wikiService.create(userDetails.getUsername(), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WikiEntryResponse> update(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody WikiEntryRequest request) {
        return ResponseEntity.ok(wikiService.update(id, request, userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        wikiService.delete(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}