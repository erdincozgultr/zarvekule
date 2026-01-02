package com.zarvekule.gamification.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.dto.QuestDto;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.entity.Quest;
import com.zarvekule.gamification.enums.QuestType;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.gamification.repository.QuestRepository;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestServiceImpl implements QuestService {

    private final QuestRepository questRepository;
    private final GuildRepository guildRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void generateWeeklyQuests(Guild guild) {
        // Haftalık 5 homebrew
        Quest homebrewQuest = new Quest();
        homebrewQuest.setGuild(guild);
        homebrewQuest.setTitle("Haftalık Homebrew");
        homebrewQuest.setDescription("Bu hafta 5 homebrew paylaşın");
        homebrewQuest.setType(QuestType.WEEKLY_HOMEBREWS);
        homebrewQuest.setTargetValue(5);
        homebrewQuest.setXpReward(200);
        homebrewQuest.setDeadline(LocalDateTime.now().plusWeeks(1));
        questRepository.save(homebrewQuest);

        // Haftalık 3 blog
        Quest blogQuest = new Quest();
        blogQuest.setGuild(guild);
        blogQuest.setTitle("Haftalık Blog");
        blogQuest.setDescription("Bu hafta 3 blog yazısı yayınlayın");
        blogQuest.setType(QuestType.WEEKLY_BLOGS);
        blogQuest.setTargetValue(3);
        blogQuest.setXpReward(150);
        blogQuest.setDeadline(LocalDateTime.now().plusWeeks(1));
        questRepository.save(blogQuest);

        log.info("Weekly quests generated for guild: {}", guild.getName());
    }

    @Override
    @Transactional
    public void generateMonthlyQuests(Guild guild) {
        // Aylık 20 homebrew
        Quest homebrewQuest = new Quest();
        homebrewQuest.setGuild(guild);
        homebrewQuest.setTitle("Aylık Homebrew Maestro");
        homebrewQuest.setDescription("Bu ay 20 homebrew paylaşın");
        homebrewQuest.setType(QuestType.MONTHLY_HOMEBREWS);
        homebrewQuest.setTargetValue(20);
        homebrewQuest.setXpReward(1000);
        homebrewQuest.setDeadline(LocalDateTime.now().plusMonths(1));
        questRepository.save(homebrewQuest);

        // Aylık 1000 XP
        Quest xpQuest = new Quest();
        xpQuest.setGuild(guild);
        xpQuest.setTitle("XP Avcısı");
        xpQuest.setDescription("Bu ay lonca olarak 1000 XP kazanın");
        xpQuest.setType(QuestType.MONTHLY_XP);
        xpQuest.setTargetValue(1000);
        xpQuest.setXpReward(500);
        xpQuest.setDeadline(LocalDateTime.now().plusMonths(1));
        questRepository.save(xpQuest);

        log.info("Monthly quests generated for guild: {}", guild.getName());
    }

    @Override
    @Transactional
    public void updateQuestProgress(Guild guild) {
        List<Quest> activeQuests = questRepository.findByGuildIdAndCompletedFalseOrderByDeadlineAsc(guild.getId());

        for (Quest quest : activeQuests) {
            int oldValue = quest.getCurrentValue();

            // İlerlemeyi hesapla (type'a göre)
            switch (quest.getType()) {
                case WEEKLY_HOMEBREWS, MONTHLY_HOMEBREWS -> {
                    // Homebrew count'u UserStats'den al
                    // Simplified: Guild member'ların toplam homebrew'ı
                    quest.setCurrentValue(calculateHomebrewCount(guild));
                }
                case WEEKLY_BLOGS -> {
                    quest.setCurrentValue(calculateBlogCount(guild));
                }
                case MONTHLY_XP -> {
                    // Guild'in başlangıçtan bu yana kazandığı XP
                    quest.setCurrentValue((int) guild.getXp());
                }
            }

            // Tamamlandı mı kontrol et
            if (quest.getCurrentValue() >= quest.getTargetValue() && !quest.isCompleted()) {
                completeQuest(quest.getId());
            }

            questRepository.save(quest);
        }
    }

    @Override
    @Transactional
    public void completeQuest(Long questId) {
        Quest quest = questRepository.findById(questId)
                .orElseThrow(() -> new ApiException("Quest bulunamadı", HttpStatus.NOT_FOUND));

        if (quest.isCompleted()) return;

        quest.setCompleted(true);
        quest.setCompletedAt(LocalDateTime.now());
        questRepository.save(quest);

        // Guild'e XP ver
        Guild guild = quest.getGuild();
        guild.setXp(guild.getXp() + quest.getXpReward());

        // Level hesapla
        int newLevel = calculateGuildLevel(guild.getXp());
        guild.setLevel(newLevel);
        guildRepository.save(guild);

        // Tüm üyelere bildirim
        guild.getMembers().forEach(member -> {
            notificationService.createNotification(
                    member,
                    "🎯 Quest Tamamlandı!",
                    quest.getTitle() + " tamamlandı! +" + quest.getXpReward() + " XP",
                    NotificationType.GUILD,
                    "/taverna/loncalar/" + guild.getId()
            );
        });

        log.info("Quest completed: {} - Guild: {}", quest.getTitle(), guild.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestDto> getGuildQuests(Long guildId) {
        List<Quest> quests = questRepository.findByGuildIdOrderByCreatedAtDesc(guildId);
        return quests.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 1 * * ?")  // Her gün saat 01:00
    public void cleanExpiredQuests() {
        List<Quest> expired = questRepository.findByDeadlineBeforeAndCompletedFalse(LocalDateTime.now());
        questRepository.deleteAll(expired);
        log.info("Cleaned {} expired quests", expired.size());
    }

    // Helper methods
    private int calculateGuildLevel(long xp) {
        if (xp < 5000) return 1;
        int level = 1;
        long xpNeeded = 0;
        while (xpNeeded <= xp) {
            level++;
            xpNeeded += 5000L * level * level;
        }
        return level - 1;
    }

    private int calculateHomebrewCount(Guild guild) {
        // Simplified - gerçekte UserStats'den toplanmalı
        return guild.getMembers().size() * 2; // Placeholder
    }

    private int calculateBlogCount(Guild guild) {
        // Simplified - gerçekte UserStats'den toplanmalı
        return guild.getMembers().size() * 1; // Placeholder
    }

    private QuestDto toDto(Quest quest) {
        QuestDto dto = new QuestDto();
        dto.setId(quest.getId());
        dto.setTitle(quest.getTitle());
        dto.setDescription(quest.getDescription());
        dto.setType(quest.getType().name());
        dto.setTargetValue(quest.getTargetValue());
        dto.setCurrentValue(quest.getCurrentValue());
        dto.setXpReward(quest.getXpReward());
        dto.setCompleted(quest.isCompleted());
        dto.setDeadline(quest.getDeadline());
        dto.setProgressPercentage(Math.min((quest.getCurrentValue() * 100) / quest.getTargetValue(), 100));
        return dto;
    }
}
