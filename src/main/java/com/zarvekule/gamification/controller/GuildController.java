package com.zarvekule.gamification.controller;

import com.zarvekule.gamification.dto.*;
import com.zarvekule.gamification.service.GuildContributionService;
import com.zarvekule.gamification.service.GuildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/guilds")
@RequiredArgsConstructor
public class GuildController {

    private final GuildService guildService;
    private final GuildContributionService contributionService;


    /**
     * Tüm guild'leri listele
     * GET /api/guilds
     */
    @GetMapping
    public ResponseEntity<List<GuildDto>> getAllGuilds(Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(guildService.getAllGuilds(username));
    }

    /**
     * Guild detayını getir
     * GET /api/guilds/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<GuildDetailDto> getGuildById(@PathVariable Long id, Principal principal) {
        String username = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(guildService.getGuildById(id, username));
    }

    /**
     * Kullanıcının guild'ini getir
     * GET /api/guilds/my-guild
     */
    @GetMapping("/my-guild")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GuildDto> getMyGuild(Principal principal) {
        return ResponseEntity.ok(guildService.getMyGuild(principal.getName()));
    }

    /**
     * Yeni guild oluştur
     * POST /api/guilds
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GuildDto> createGuild(
            Principal principal,
            @Valid @RequestBody GuildCreateRequest request) {
        GuildDto created = guildService.createGuild(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Guild'i güncelle (sadece leader)
     * PUT /api/guilds/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GuildDto> updateGuild(
            Principal principal,
            @PathVariable Long id,
            @Valid @RequestBody GuildUpdateRequest request) {
        return ResponseEntity.ok(guildService.updateGuild(principal.getName(), id, request));
    }

    /**
     * Guild'i sil (sadece leader)
     * DELETE /api/guilds/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteGuild(Principal principal, @PathVariable Long id) {
        guildService.deleteGuild(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Guild'e katıl
     * POST /api/guilds/{id}/join
     */
    @PostMapping("/{id}/join")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> joinGuild(Principal principal, @PathVariable Long id) {
        guildService.joinGuild(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * Guild'den ayrıl
     * POST /api/guilds/{id}/leave
     */
    @PostMapping("/{id}/leave")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> leaveGuild(Principal principal, @PathVariable Long id) {
        guildService.leaveGuild(principal.getName(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * Üyeyi guild'den at (sadece leader)
     * DELETE /api/guilds/{id}/members/{memberId}
     */
    @DeleteMapping("/{id}/members/{memberId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> kickMember(
            Principal principal,
            @PathVariable Long id,
            @PathVariable Long memberId) {
        guildService.kickMember(principal.getName(), id, memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/contributions")
    public ResponseEntity<GuildContributionsDto> getGuildContributions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "monthly") String period) {
        return ResponseEntity.ok(contributionService.getGuildContributions(id, period));
    }
}