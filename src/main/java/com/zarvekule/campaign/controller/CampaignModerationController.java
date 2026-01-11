package com.zarvekule.campaign.controller;

import com.zarvekule.campaign.service.CampaignModerationService;
import com.zarvekule.moderation.dto.ModerationAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Campaign Moderasyon Controller
 */
@RestController
@RequestMapping("/api/mod/campaigns")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class CampaignModerationController {

    private final CampaignModerationService moderationService;

    /**
     * Campaign sil
     * DELETE /api/mod/campaigns/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.deleteCampaign(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }
}