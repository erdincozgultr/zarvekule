package com.zarvekule.homebrew.controller;

import com.zarvekule.homebrew.dto.HomebrewEntryPatchRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.service.HomebrewEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    // --- PUBLIC LİSTELEME ENDPOINTLERİ (Giriş yapanın beğenileri görünür) ---

    @GetMapping("/public")
    public ResponseEntity<List<HomebrewEntryResponse>> getAllPublished(Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getPublishedHomebrews(username));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<HomebrewEntryResponse>> getByCategory(
            @PathVariable HomebrewCategory category,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getPublishedHomebrewsByCategory(category, username));
    }

    @GetMapping("/read/{slug}")
    public ResponseEntity<HomebrewEntryResponse> getBySlug(
            @PathVariable String slug,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.getBySlug(slug, username));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HomebrewEntryResponse>> search(
            @RequestParam String q,
            Principal principal) {
        String username = (principal != null) ? principal.getName() : null;
        return ResponseEntity.ok(homebrewService.search(q, username));
    }

    // --- YÖNETİM ENDPOINTLERİ ---

    @PostMapping
    public ResponseEntity<HomebrewEntryResponse> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody HomebrewEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(homebrewService.create(userDetails.getUsername(), request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HomebrewEntryResponse> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody HomebrewEntryPatchRequest request) {
        return ResponseEntity.ok(homebrewService.update(userDetails.getUsername(), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        homebrewService.delete(userDetails.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-homebrews")
    public ResponseEntity<List<HomebrewEntryResponse>> getMyHomebrews(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(homebrewService.getMyHomebrews(userDetails.getUsername()));
    }

    @GetMapping("/categories/{category}/count")
    public ResponseEntity<Long> countByCategory(@PathVariable HomebrewCategory category) {
        return ResponseEntity.ok(homebrewService.countPublishedHomebrewsByCategory(category));
    }

//    @PostMapping("/{id}/fork")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<HomebrewEntryResponse> forkEntry(Principal principal, @PathVariable Long id) {
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(homebrewService.forkEntry(principal.getName(), id));
//    }
}