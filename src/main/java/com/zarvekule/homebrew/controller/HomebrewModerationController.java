package com.zarvekule.homebrew.controller;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.mapper.HomebrewEntryMapper;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.homebrew.service.HomebrewModerationService;
import com.zarvekule.moderation.dto.ModerationAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * Homebrew Moderasyon Controller
 * <p>
 * Tüm endpoint'ler ROLE_MODERATOR veya ROLE_ADMIN gerektirir
 */
@RestController
@RequestMapping("/api/mod/homebrews")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class HomebrewModerationController {

    private final HomebrewModerationService moderationService;
    private final HomebrewEntryRepository homebrewRepository;
    private final HomebrewEntryMapper homebrewMapper;

    /**
     * Homebrew'ı onayla
     * POST /api/mod/homebrews/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.approveHomebrew(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Homebrew'ı reddet
     * POST /api/mod/homebrews/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> reject(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.rejectHomebrew(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Homebrew'ı drafta al
     * POST /api/mod/homebrews/{id}/move-to-draft
     */
    @PostMapping("/{id}/move-to-draft")
    public ResponseEntity<Void> moveToDraft(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.moveToDraft(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Homebrew metadata düzenle
     * PATCH /api/mod/homebrews/{id}/metadata
     */
    @PatchMapping("/{id}/metadata")
    public ResponseEntity<Void> updateMetadata(
            Principal principal,
            @PathVariable Long id,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.updateMetadata(
                principal.getName(), id, title, description, category, action
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<HomebrewEntryResponse> getHomebrewById(@PathVariable Long id) {
        HomebrewEntry homebrew = homebrewRepository.findById(id)
                .orElseThrow(() -> new ApiException("Homebrew bulunamadı", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(homebrewMapper.toResponseDto(homebrew));
    }

    /**
     * Homebrew'ı slug ile getir (tüm status'lar için)
     * Moderatörler draft/pending içerikleri görüntüleyebilir
     *
     * GET /api/mod/homebrews/preview/{slug}
     */
    @GetMapping("/preview/{slug}")
    public ResponseEntity<HomebrewEntryResponse> getHomebrewBySlug(@PathVariable String slug) {
        HomebrewEntry homebrew = homebrewRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException("Homebrew bulunamadı", HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(homebrewMapper.toResponseDto(homebrew));
    }
}