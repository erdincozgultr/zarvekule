package com.zarvekule.community.controller;

import com.zarvekule.community.dto.WallCommentRequest;
import com.zarvekule.community.dto.WallCommentResponse;
import com.zarvekule.community.service.WallCommentService;
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
@RequestMapping("/api/wall")
@RequiredArgsConstructor
public class WallCommentController {

    private final WallCommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<WallCommentResponse> postComment(Principal principal,
                                                           @Valid @RequestBody WallCommentRequest request) {
        WallCommentResponse response = commentService.postComment(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{username}")
    public ResponseEntity<Page<WallCommentResponse>> getComments(@PathVariable("username") String profileOwnerUsername,
                                                                 Pageable pageable) {
        Page<WallCommentResponse> comments = commentService.getCommentsByProfileOwner(profileOwnerUsername, pageable);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteComment(Principal principal,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(principal.getName(), commentId);
        return ResponseEntity.noContent().build();
    }
}