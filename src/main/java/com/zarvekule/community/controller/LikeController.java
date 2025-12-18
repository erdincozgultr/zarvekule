package com.zarvekule.community.controller;

import com.zarvekule.community.dto.LikeRequest;
import com.zarvekule.community.enums.TargetType;
import com.zarvekule.community.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> toggleLike(Principal principal,
                                                           @Valid @RequestBody LikeRequest request) {

        boolean isLiked = likeService.toggleLike(principal.getName(), request);

        return ResponseEntity.ok(Map.of("isLiked", isLiked));
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<Map<String, Long>> getLikeCount(
            @PathVariable TargetType targetType,
            @PathVariable Long targetId) {

        long count = likeService.getLikeCount(targetType, targetId);

        return ResponseEntity.ok(Map.of("likeCount", count));
    }
}