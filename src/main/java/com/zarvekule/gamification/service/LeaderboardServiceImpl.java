package com.zarvekule.gamification.service;

import com.zarvekule.gamification.dto.ContentLeaderboardDto;
import com.zarvekule.gamification.dto.GuildLeaderboardDto;
import com.zarvekule.gamification.dto.LeaderboardEntryDto;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.entity.UserStats;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.gamification.repository.QuestRepository;
import com.zarvekule.gamification.repository.UserBadgeRepository;
import com.zarvekule.gamification.repository.UserStatsRepository;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final UserStatsRepository statsRepository;
    private final HomebrewEntryRepository homebrewRepository;
    private final GuildRepository guildRepository;
    private final UserMapper userMapper;
    private final UserBadgeRepository userBadgeRepository;
    private final QuestRepository questRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getTopUsersByXp(int limit) {
        // Repository'de zaten var: findAllByOrderByCurrentXpDesc()
        List<UserStats> topStats = statsRepository.findAllByOrderByCurrentXpDesc();

        return topStats.stream()
                .limit(limit)
                .map(stats -> {
                    LeaderboardEntryDto dto = new LeaderboardEntryDto();
                    dto.setRank(topStats.indexOf(stats) + 1);
                    dto.setUser(userMapper.toSummaryDto(stats.getUser()));
                    dto.setValue(stats.getCurrentXp());
                    dto.setRankTier(stats.getCurrentRank().getTitle());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getTopUsersByLikesReceived(int limit) {
        List<UserStats> allStats = statsRepository.findAll();

        // Beğeniye göre sırala
        List<UserStats> sortedStats = allStats.stream()
                .sorted(Comparator.comparingInt(UserStats::getTotalLikesReceived).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        List<LeaderboardEntryDto> result = new ArrayList<>();
        for (int i = 0; i < sortedStats.size(); i++) {
            UserStats stats = sortedStats.get(i);
            LeaderboardEntryDto dto = new LeaderboardEntryDto();
            dto.setRank(i + 1);
            dto.setUser(userMapper.toSummaryDto(stats.getUser()));
            dto.setValue(stats.getTotalLikesReceived());
            dto.setRankTier(stats.getCurrentRank().getTitle());
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getTopUsersByContentCount(int limit) {
        List<UserStats> allStats = statsRepository.findAll();

        // Toplam içerik sayısına göre sırala (homebrews + blogs)
        List<UserStats> sortedStats = allStats.stream()
                .sorted(Comparator.comparingInt((UserStats s) ->
                        s.getTotalHomebrews() + s.getTotalBlogs()).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        List<LeaderboardEntryDto> result = new ArrayList<>();
        for (int i = 0; i < sortedStats.size(); i++) {
            UserStats stats = sortedStats.get(i);
            LeaderboardEntryDto dto = new LeaderboardEntryDto();
            dto.setRank(i + 1);
            dto.setUser(userMapper.toSummaryDto(stats.getUser()));
            dto.setValue(stats.getTotalHomebrews() + stats.getTotalBlogs());
            dto.setRankTier(stats.getCurrentRank().getTitle());
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentLeaderboardDto> getTopHomebrewsByLikes(int limit) {
        // Sadece PUBLISHED homebrewları al, beğeniye göre sırala
        List<HomebrewEntry> allHomebrews = homebrewRepository
                .findAllByStatusOrderByPublishedAtDesc(HomebrewStatus.PUBLISHED);

        List<HomebrewEntry> topHomebrews = allHomebrews.stream()
                .sorted(Comparator.comparingLong(HomebrewEntry::getLikeCount).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        List<ContentLeaderboardDto> result = new ArrayList<>();
        for (int i = 0; i < topHomebrews.size(); i++) {
            HomebrewEntry entry = topHomebrews.get(i);
            ContentLeaderboardDto dto = new ContentLeaderboardDto();
            dto.setRank(i + 1);
            dto.setContentId(entry.getId());
            dto.setTitle(entry.getName());
            dto.setSlug(entry.getSlug());
            dto.setAuthorUsername(entry.getAuthor().getUsername());
            dto.setLikeCount(entry.getLikeCount());
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuildLeaderboardDto> getTopGuildsByLevel(int limit) {
        List<Guild> allGuilds = guildRepository.findAll();

        // Seviyeye göre sırala, aynı seviyedeyse XP'ye göre
        List<Guild> topGuilds = allGuilds.stream()
                .sorted(Comparator.comparingInt(Guild::getLevel).reversed()
                        .thenComparingLong(Guild::getXp).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        List<GuildLeaderboardDto> result = new ArrayList<>();
        for (int i = 0; i < topGuilds.size(); i++) {
            Guild guild = topGuilds.get(i);
            GuildLeaderboardDto dto = new GuildLeaderboardDto();
            dto.setRank(i + 1);
            dto.setGuildId(guild.getId());
            dto.setGuildName(guild.getName());
            dto.setLevel(guild.getLevel());
            dto.setXp(guild.getXp());
            dto.setMemberCount(guild.getMembers() != null ? guild.getMembers().size() : 0);
            dto.setLeaderUsername(guild.getLeader() != null ? guild.getLeader().getUsername() : "N/A");
            result.add(dto);
        }

        return result;
    }


    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getTopUsersByBadges(int limit) {
        // Kullanıcıların badge sayılarını hesapla
        List<UserStats> allStats = statsRepository.findAll();

        // Her kullanıcının badge count'unu hesapla ve sırala
        List<UserStats> sortedStats = allStats.stream()
                .sorted((s1, s2) -> {
                    int badges1 = userBadgeRepository.countByUser_Id(s1.getUser().getId());
                    int badges2 = userBadgeRepository.countByUser_Id(s2.getUser().getId());
                    return Integer.compare(badges2, badges1);
                })
                .limit(limit)
                .collect(Collectors.toList());

        List<LeaderboardEntryDto> result = new ArrayList<>();
        for (int i = 0; i < sortedStats.size(); i++) {
            UserStats stats = sortedStats.get(i);
            int badgeCount = userBadgeRepository.countByUser_Id(stats.getUser().getId());

            LeaderboardEntryDto dto = new LeaderboardEntryDto();
            dto.setRank(i + 1);
            dto.setUser(userMapper.toSummaryDto(stats.getUser()));
            dto.setValue(badgeCount);
            dto.setRankTier(stats.getCurrentRank().getTitle());
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LeaderboardEntryDto> getTopUsersByComments(int limit) {
        List<UserStats> allStats = statsRepository.findAll();

        // Yorum sayısına göre sırala
        List<UserStats> sortedStats = allStats.stream()
                .sorted(Comparator.comparingInt(UserStats::getTotalComments).reversed())
                .limit(limit)
                .collect(Collectors.toList());

        List<LeaderboardEntryDto> result = new ArrayList<>();
        for (int i = 0; i < sortedStats.size(); i++) {
            UserStats stats = sortedStats.get(i);
            LeaderboardEntryDto dto = new LeaderboardEntryDto();
            dto.setRank(i + 1);
            dto.setUser(userMapper.toSummaryDto(stats.getUser()));
            dto.setValue(stats.getTotalComments());
            dto.setRankTier(stats.getCurrentRank().getTitle());
            result.add(dto);
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GuildLeaderboardDto> getTopGuildsByQuestsCompleted(int limit) {
        List<Guild> allGuilds = guildRepository.findAll();

        // Her guild'in tamamlanmış quest sayısını hesapla ve sırala
        List<Guild> sortedGuilds = allGuilds.stream()
                .sorted((g1, g2) -> {
                    long quests1 = questRepository.countByGuild_IdAndCompletedTrue(g1.getId());
                    long quests2 = questRepository.countByGuild_IdAndCompletedTrue(g2.getId());
                    return Long.compare(quests2, quests1);
                })
                .limit(limit)
                .collect(Collectors.toList());

        List<GuildLeaderboardDto> result = new ArrayList<>();
        for (int i = 0; i < sortedGuilds.size(); i++) {
            Guild guild = sortedGuilds.get(i);
            long questsCompleted = questRepository.countByGuild_IdAndCompletedTrue(guild.getId());

            GuildLeaderboardDto dto = new GuildLeaderboardDto();
            dto.setRank(i + 1);
            dto.setGuildId(guild.getId());
            dto.setGuildName(guild.getName());
            dto.setLevel(guild.getLevel());
            dto.setXp(guild.getXp());
            dto.setMemberCount(guild.getMembers() != null ? guild.getMembers().size() : 0);
            dto.setLeaderUsername(guild.getLeader() != null ? guild.getLeader().getUsername() : "N/A");
            dto.setQuestsCompleted((int) questsCompleted); // ✅ YENİ field
            result.add(dto);
        }

        return result;
    }
}