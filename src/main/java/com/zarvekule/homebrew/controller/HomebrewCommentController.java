package com.zarvekule.homebrew.controller;

import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.homebrew.dto.HomebrewCommentRequest;
import com.zarvekule.homebrew.service.HomebrewCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/homebrew-comments")
@RequiredArgsConstructor
public class HomebrewCommentController {

    private final HomebrewCommentService commentService;

    /**
     * Homebrew için yorumları getir (pagination)
     * GET /api/homebrew-comments/homebrew/{homebrewId}?page=0&size=20
     */
    @GetMapping("/homebrew/{homebrewId}")
    public ResponseEntity<Page<CommentDto>> getComments(
            @PathVariable Long homebrewId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(commentService.getCommentsForHomebrew(homebrewId, pageable));
    }

    /**
     * Yorum ekle (AUTH)
     * POST /api/homebrew-comments
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addComment(
            Authentication auth,
            @Valid @RequestBody HomebrewCommentRequest request
    ) {
        commentService.addComment(auth.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Yorum sil (AUTH - kendi yorumu veya ADMIN/MODERATOR)
     * DELETE /api/homebrew-comments/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(
            Authentication auth,
            @PathVariable Long id
    ) {
        commentService.deleteComment(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Yorum onayla (MODERATOR/ADMIN)
     * PATCH /api/homebrew-comments/{id}/approve
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<Void> approveComment(
            Authentication auth,
            @PathVariable Long id
    ) {
        commentService.approveComment(auth.getName(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * Onay bekleyen yorumlar (MODERATOR/ADMIN)
     * GET /api/homebrew-comments/pending?page=0&size=20
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public ResponseEntity<Page<CommentDto>> getPendingComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(commentService.getPendingComments(pageable));
    }
}