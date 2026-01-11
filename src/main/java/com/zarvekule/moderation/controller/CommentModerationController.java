package com.zarvekule.moderation.controller;

import com.zarvekule.moderation.dto.ModerationAction;
import com.zarvekule.moderation.service.CommentModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Comment Moderation Controller
 *
 * Tüm yorum tipleri için moderasyon endpoint'leri
 */
@RestController
@RequestMapping("/api/mod/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class CommentModerationController {

    private final CommentModerationService commentModerationService;

    /**
     * Homebrew yorumu sil
     * DELETE /api/mod/comments/homebrew/{commentId}
     */
    @DeleteMapping("/homebrew/{commentId}")
    public ResponseEntity<Void> deleteHomebrewComment(
            Principal principal,
            @PathVariable Long commentId,
            @Valid @RequestBody ModerationAction action
    ) {
        commentModerationService.deleteHomebrewComment(principal.getName(), commentId, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Wiki yorumu sil
     * DELETE /api/mod/comments/wiki/{commentId}
     */
    @DeleteMapping("/wiki/{commentId}")
    public ResponseEntity<Void> deleteWikiComment(
            Principal principal,
            @PathVariable Long commentId,
            @Valid @RequestBody ModerationAction action
    ) {
        commentModerationService.deleteWikiComment(principal.getName(), commentId, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Blog yorumu sil
     * DELETE /api/mod/comments/blog/{commentId}
     */
    @DeleteMapping("/blog/{commentId}")
    public ResponseEntity<Void> deleteBlogComment(
            Principal principal,
            @PathVariable Long commentId,
            @Valid @RequestBody ModerationAction action
    ) {
        commentModerationService.deleteBlogComment(principal.getName(), commentId, action);
        return ResponseEntity.ok().build();
    }
}