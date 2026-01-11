package com.zarvekule.moderation.controller;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.campaign.dto.CampaignResponse;
import com.zarvekule.gamification.dto.GuildDto;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.moderation.dto.ModerationStatsDTO;
import com.zarvekule.moderation.service.ModerationDashboardService;
import com.zarvekule.moderation.service.ModerationStatsService;
import com.zarvekule.venue.dto.VenueResponse;
import com.zarvekule.venue.enums.VenueStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Moderation Dashboard Controller - FINAL VERSION
 * Tüm modüller + Stats endpoint
 */
@RestController
@RequestMapping("/api/mod/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class ModerationDashboardController {

    private final ModerationDashboardService dashboardService;
    private final ModerationStatsService statsService;

    // ============================================
    // STATS
    // ============================================

    /**
     * Moderasyon istatistiklerini getir
     * GET /api/mod/dashboard/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ModerationStatsDTO> getStats() {
        return ResponseEntity.ok(statsService.getStats());
    }

    // ============================================
    // HOMEBREW
    // ============================================

    @GetMapping("/homebrews/pending")
    public ResponseEntity<Page<HomebrewEntryResponse>> getPendingHomebrews(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getPendingHomebrews(pageable));
    }

    @GetMapping("/homebrews")
    public ResponseEntity<Page<HomebrewEntryResponse>> getAllHomebrews(
            @RequestParam(required = false) HomebrewStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(dashboardService.getAllHomebrews(status, pageable));
    }

    // ============================================
    // BLOG
    // ============================================

    @GetMapping("/blogs")
    public ResponseEntity<Page<BlogEntrySummary>> getAllBlogs(
            @RequestParam(required = false) BlogStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(dashboardService.getAllBlogs(status, pageable));
    }

    @GetMapping("/blogs/published")
    public ResponseEntity<Page<BlogEntrySummary>> getPublishedBlogs(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getPublishedBlogs(pageable));
    }

    // ============================================
    // GUILD
    // ============================================

    @GetMapping("/guilds")
    public ResponseEntity<Page<GuildDto>> getAllGuilds(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getAllGuilds(pageable));
    }

    @GetMapping("/guilds/banned")
    public ResponseEntity<Page<GuildDto>> getBannedGuilds(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getBannedGuilds(pageable));
    }

    // ============================================
    // CAMPAIGN
    // ============================================

    @GetMapping("/campaigns")
    public ResponseEntity<Page<CampaignResponse>> getAllCampaigns(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getAllCampaigns(pageable));
    }

    // ============================================
    // VENUE
    // ============================================

    @GetMapping("/venues/pending")
    public ResponseEntity<Page<VenueResponse>> getPendingVenues(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getPendingVenues(pageable));
    }

    @GetMapping("/venues")
    public ResponseEntity<Page<VenueResponse>> getAllVenues(
            @RequestParam(required = false) VenueStatus status,
            Pageable pageable
    ) {
        return ResponseEntity.ok(dashboardService.getAllVenues(status, pageable));
    }

    // ============================================
    // AUDIT
    // ============================================

    @GetMapping("/audit")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam String targetType,
            @RequestParam Long targetId
    ) {
        return ResponseEntity.ok(dashboardService.getAuditLogs(targetType, targetId));
    }

    @GetMapping("/audit/all")
    public ResponseEntity<Page<AuditLog>> getAllAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(dashboardService.getAllAuditLogs(pageable));
    }
}