package com.zarvekule.gamification.controller;

import com.zarvekule.gamification.dto.QuestDto;
import com.zarvekule.gamification.service.QuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guilds/{guildId}/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    /**
     * Guild quest'lerini getir
     * GET /api/guilds/{guildId}/quests
     */
    @GetMapping
    public ResponseEntity<List<QuestDto>> getGuildQuests(@PathVariable Long guildId) {
        return ResponseEntity.ok(questService.getGuildQuests(guildId));
    }
}