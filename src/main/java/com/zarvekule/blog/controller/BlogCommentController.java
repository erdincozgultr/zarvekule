package com.zarvekule.blog.controller;

import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.blog.dto.CommentRequest;
import com.zarvekule.blog.service.BlogCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class BlogCommentController {

    private final BlogCommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addComment(Principal principal,
                                           @Valid @RequestBody CommentRequest request) {
        commentService.addComment(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/blog/{blogId}")
    public ResponseEntity<Page<CommentDto>> getComments(@PathVariable Long blogId, Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsForBlog(blogId, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(Principal principal,
                                              @PathVariable Long id) {
        commentService.deleteComment(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    // --- GÜNCELLENEN KISIMLAR ---

    // Admin VEYA Moderatör: Yorumu onayla
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<Void> approveComment(Principal principal, @PathVariable Long id) {
        commentService.approveComment(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    // Admin VEYA Moderatör: Onay bekleyen yorumları gör
    @GetMapping("/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MODERATOR')")
    public ResponseEntity<Page<CommentDto>> getPendingComments(Pageable pageable) {
        return ResponseEntity.ok(commentService.getPendingComments(pageable));
    }
}