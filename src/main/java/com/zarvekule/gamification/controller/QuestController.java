// QuestController.java - MANUAL QUEST GENERATION ENDPOINT

package com.zarvekule.gamification.controller;

import com.zarvekule.gamification.dto.QuestDto;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.gamification.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guilds/{guildId}/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;
    private final GuildRepository guildRepository;

    /**
     * Guild quest'lerini getir
     * GET /api/guilds/{guildId}/quests
     */
    @GetMapping
    public ResponseEntity<List<QuestDto>> getGuildQuests(@PathVariable Long guildId) {
        return ResponseEntity.ok(questService.getGuildQuests(guildId));
    }

    /**
     * ✅ YENİ: Manuel quest generation (mevcut guild'ler için)
     * POST /api/guilds/{guildId}/quests/generate
     *
     * Bu endpoint ile mevcut guild'ler için quest'ler generate edilebilir
     * Normalde guild oluşturulurken otomatik yapılır
     */
    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> generateQuests(@PathVariable Long guildId) {
        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new RuntimeException("Guild not found"));

        questService.generateWeeklyQuests(guild);
        questService.generateMonthlyQuests(guild);

        return ResponseEntity.ok("Quests generated successfully");
    }
}