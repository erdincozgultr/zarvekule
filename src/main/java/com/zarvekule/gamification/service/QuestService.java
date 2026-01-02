package com.zarvekule.gamification.service;

import com.zarvekule.gamification.dto.QuestDto;
import com.zarvekule.gamification.entity.Guild;
import java.util.List;

public interface QuestService {
    void generateWeeklyQuests(Guild guild);
    void generateMonthlyQuests(Guild guild);
    void updateQuestProgress(Guild guild);
    void completeQuest(Long questId);
    List<QuestDto> getGuildQuests(Long guildId);
    void cleanExpiredQuests();
}