package com.zarvekule.gamification.controller;

import com.zarvekule.gamification.dto.ContentLeaderboardDto;
import com.zarvekule.gamification.dto.GuildLeaderboardDto;
import com.zarvekule.gamification.dto.LeaderboardEntryDto;
import com.zarvekule.gamification.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    /**
     * XP'ye göre top kullanıcılar
     * GET /api/leaderboard/users/xp?limit=50
     */
    @GetMapping("/users/xp")
    public ResponseEntity<List<LeaderboardEntryDto>> getTopUsersByXp(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopUsersByXp(limit));
    }

    /**
     * Aldığı beğeniye göre top kullanıcılar
     * GET /api/leaderboard/users/likes?limit=50
     */
    @GetMapping("/users/likes")
    public ResponseEntity<List<LeaderboardEntryDto>> getTopUsersByLikes(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopUsersByLikesReceived(limit));
    }

    /**
     * Ürettiği içerik sayısına göre top kullanıcılar
     * GET /api/leaderboard/users/content?limit=50
     */
    @GetMapping("/users/content")
    public ResponseEntity<List<LeaderboardEntryDto>> getTopUsersByContent(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopUsersByContentCount(limit));
    }

    /**
     * Beğeniye göre top homebrew içerikler
     * GET /api/leaderboard/content/homebrews?limit=50
     */
    @GetMapping("/content/homebrews")
    public ResponseEntity<List<ContentLeaderboardDto>> getTopHomebrews(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopHomebrewsByLikes(limit));
    }

    /**
     * Seviyeye göre top guild'ler
     * GET /api/leaderboard/guilds/level?limit=50
     */
    @GetMapping("/guilds/level")
    public ResponseEntity<List<GuildLeaderboardDto>> getTopGuildsByLevel(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopGuildsByLevel(limit));
    }

    /**
     * ✅ YENİ: Rozet sayısına göre top kullanıcılar
     * GET /api/leaderboard/users/badges?limit=50
     */
    @GetMapping("/users/badges")
    public ResponseEntity<List<LeaderboardEntryDto>> getTopUsersByBadges(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopUsersByBadges(limit));
    }

    /**
     * ✅ YENİ: Yorum sayısına göre top kullanıcılar
     * GET /api/leaderboard/users/comments?limit=50
     */
    @GetMapping("/users/comments")
    public ResponseEntity<List<LeaderboardEntryDto>> getTopUsersByComments(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopUsersByComments(limit));
    }

    /**
     * ✅ YENİ: Tamamlanan quest sayısına göre top guild'ler
     * GET /api/leaderboard/guilds/quests?limit=50
     */
    @GetMapping("/guilds/quests")
    public ResponseEntity<List<GuildLeaderboardDto>> getTopGuildsByQuests(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(leaderboardService.getTopGuildsByQuestsCompleted(limit));
    }
}