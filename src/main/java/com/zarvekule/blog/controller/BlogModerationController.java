package com.zarvekule.blog.controller;

import com.zarvekule.blog.service.BlogModerationService;
import com.zarvekule.moderation.dto.ModerationAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Blog Moderasyon Controller
 *
 * Tüm endpoint'ler ROLE_MODERATOR veya ROLE_ADMIN gerektirir
 */
@RestController
@RequestMapping("/api/mod/blogs")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class BlogModerationController {

    private final BlogModerationService moderationService;

    /**
     * Blog'u drafta al
     * POST /api/mod/blogs/{id}/move-to-draft
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
     * Blog metadata düzenle
     * PATCH /api/mod/blogs/{id}/metadata
     */
    @PatchMapping("/{id}/metadata")
    public ResponseEntity<Void> updateMetadata(
            Principal principal,
            @PathVariable Long id,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String featuredImage,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.updateMetadata(
                principal.getName(), id, category, slug, tags, featuredImage, action
        );
        return ResponseEntity.ok().build();
    }
}