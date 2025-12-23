package com.zarvekule.gamification.controller;

import com.zarvekule.gamification.dto.BadgeDto;
import com.zarvekule.gamification.service.BadgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {

    private final BadgeService badgeService;

    /**
     * Tüm badge'leri listele (public)
     * GET /api/badges
     */
    @GetMapping
    public ResponseEntity<List<BadgeDto>> getAllBadges() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    /**
     * Kullanıcının badge'lerini listele (authenticated)
     * Tüm badge'leri gösterir, kullanıcının kazandıkları earned=true olur
     * GET /api/badges/my-badges
     */
    @GetMapping("/my-badges")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<BadgeDto>> getMyBadges(Principal principal) {
        return ResponseEntity.ok(badgeService.getUserBadges(principal.getName()));
    }
}