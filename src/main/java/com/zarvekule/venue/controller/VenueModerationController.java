package com.zarvekule.venue.controller;

import com.zarvekule.moderation.dto.ModerationAction;
import com.zarvekule.venue.service.VenueModerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Venue Moderasyon Controller
 */
@RestController
@RequestMapping("/api/mod/venues")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class VenueModerationController {

    private final VenueModerationService moderationService;

    /**
     * Venue onayla
     * POST /api/mod/venues/{id}/approve
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approveVenue(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.approveVenue(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Venue reddet
     * POST /api/mod/venues/{id}/reject
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<Void> rejectVenue(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.rejectVenue(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Venue'yü drafta al
     * POST /api/mod/venues/{id}/move-to-draft
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
}