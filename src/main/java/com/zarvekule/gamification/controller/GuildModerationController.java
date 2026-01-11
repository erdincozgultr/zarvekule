package com.zarvekule.gamification.controller;

import com.zarvekule.gamification.service.GuildModerationService;
import com.zarvekule.moderation.dto.ModerationAction;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Guild Moderasyon Controller
 */
@RestController
@RequestMapping("/api/mod/guilds")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MODERATOR') or hasAuthority('ROLE_ADMIN')")
public class GuildModerationController {

    private final GuildModerationService moderationService;

    /**
     * Guild'i soft ban yap
     * POST /api/mod/guilds/{id}/ban
     */
    @PostMapping("/{id}/ban")
    public ResponseEntity<Void> banGuild(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.banGuild(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }

    /**
     * Guild yasağını kaldır
     * POST /api/mod/guilds/{id}/unban
     */
    @PostMapping("/{id}/unban")
    public ResponseEntity<Void> unbanGuild(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody ModerationAction action
    ) {
        moderationService.unbanGuild(principal.getName(), id, action);
        return ResponseEntity.ok().build();
    }
}