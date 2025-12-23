package com.zarvekule.user.dto;

import com.zarvekule.gamification.dto.BadgeDto;
import com.zarvekule.gamification.dto.GuildSummaryDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfileDto {
    // Temel kullanıcı bilgileri
    private String username;
    private String displayName;
    private String bio;
    private String avatarUrl;
    private String bannerUrl;
    private String title;
    private LocalDateTime joinedAt;
    private List<String> roles;

    // Gamification bilgileri
    private UserStatsDto stats;           // XP, rank, istatistikler
    private List<BadgeDto> badges;        // Rozetler
    private GuildSummaryDto guild;        // Lonca bilgisi (varsa)
}