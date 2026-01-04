package com.zarvekule.gamification.service;

import com.zarvekule.gamification.dto.GuildContributionsDto;
import com.zarvekule.gamification.dto.TopContributorDto;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.campaign.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GuildContributionService {

    private final GuildRepository guildRepository;
    private final HomebrewEntryRepository homebrewRepository;
    private final BlogEntryRepository blogRepository;
    private final CampaignRepository campaignRepository;

    @Transactional(readOnly = true)
    public GuildContributionsDto getGuildContributions(Long guildId, String period) {
        Guild guild = guildRepository.findByIdWithMembers(guildId)
                .orElseThrow(() -> new RuntimeException("Guild not found"));

        LocalDateTime startDate = calculateStartDate(period);

        // Lonca üyelerinin ID listesi
        List<Long> memberIds = guild.getMembers().stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Bu dönem istatistikleri
        long homebrewsCount = homebrewRepository.countByAuthorIdInAndCreatedAtAfter(memberIds, startDate);
        long blogsCount = blogRepository.countByAuthorIdInAndCreatedAtAfter(memberIds, startDate);
        long campaignsCount = campaignRepository.countByDungeonMaster_IdInAndCreatedAtAfter(memberIds, startDate);

        // Son paylaşılan içerikler
        List<Object> recentHomebrews = homebrewRepository
                .findTop10ByAuthorIdInOrderByCreatedAtDesc(memberIds)
                .stream()
                .map(h -> new RecentContentDto(
                        h.getId(),
                        h.getName(),
                        h.getSlug(),
                        h.getAuthor().getDisplayName() != null ? h.getAuthor().getDisplayName() : h.getAuthor().getUsername(),
                        h.getAuthor().getUsername(),
                        h.getCreatedAt(),
                        0, // likeCount - HomebrewEntry'de yok, 0 vereceğiz
                        "homebrew"
                ))
                .collect(Collectors.toList());

        List<Object> recentBlogs = blogRepository
                .findTop10ByAuthorIdInOrderByCreatedAtDesc(memberIds)
                .stream()
                .map(b -> new RecentContentDto(
                        b.getId(),
                        b.getTitle(),
                        b.getSlug(),
                        b.getAuthor().getDisplayName() != null ? b.getAuthor().getDisplayName() : b.getAuthor().getUsername(),
                        b.getAuthor().getUsername(),
                        b.getCreatedAt(),
                        0, // likeCount
                        "blog"
                ))
                .collect(Collectors.toList());

        // Top katkıcılar (XP bazında)
        List<TopContributorDto> topContributors = calculateTopContributors(guild, startDate);

        return new GuildContributionsDto(
                homebrewsCount,
                blogsCount,
                campaignsCount,
                recentHomebrews,
                recentBlogs,
                topContributors
        );
    }

    private LocalDateTime calculateStartDate(String period) {
        return switch (period.toLowerCase()) {
            case "weekly" -> LocalDateTime.now().minusWeeks(1);
            case "monthly" -> LocalDateTime.now().minusMonths(1);
            case "alltime" -> LocalDateTime.of(2000, 1, 1, 0, 0);
            default -> LocalDateTime.now().minusMonths(1); // default: monthly
        };
    }

    private List<TopContributorDto> calculateTopContributors(Guild guild, LocalDateTime startDate) {
        // Her üyenin bu dönem kazandığı XP'yi hesapla
        List<TopContributorDto> contributors = new ArrayList<>();

        for (User member : guild.getMembers()) {
            // Dönem içindeki içerik sayıları
            long homebrewCount = homebrewRepository.countByAuthorIdAndCreatedAtAfter(
                    member.getId(), startDate);
            long blogCount = blogRepository.countByAuthorIdAndCreatedAtAfter(
                    member.getId(), startDate);
            long campaignCount = campaignRepository.countByDungeonMaster_IdAndCreatedAtAfter(member.getId(), startDate);

            // XP hesapla (basitleştirilmiş)
            long xpContributed = (homebrewCount * 100) + (blogCount * 80) + (campaignCount * 50);
            long contentCount = homebrewCount + blogCount + campaignCount;

            if (xpContributed > 0) {
                contributors.add(new TopContributorDto(
                        member.getId(),
                        member.getUsername(),
                        member.getDisplayName() != null ? member.getDisplayName() : member.getUsername(),
                        member.getAvatarUrl(),
                        xpContributed,
                        contentCount
                ));
            }
        }

        // XP'ye göre sırala ve top 10 al
        return contributors.stream()
                .sorted((a, b) -> Long.compare(b.getXpContributed(), a.getXpContributed()))
                .limit(10)
                .collect(Collectors.toList());
    }

    // Inner DTO class
    @lombok.Data
    @lombok.AllArgsConstructor
    public static class RecentContentDto {
        private Long id;
        private String title;
        private String slug;
        private String authorName;
        private String authorUsername;
        private LocalDateTime createdAt;
        private int likeCount;
        private String type; // "homebrew" or "blog"
    }
}