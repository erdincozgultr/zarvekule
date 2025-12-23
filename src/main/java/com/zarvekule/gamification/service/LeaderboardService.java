package com.zarvekule.gamification.service;

import com.zarvekule.gamification.dto.ContentLeaderboardDto;
import com.zarvekule.gamification.dto.GuildLeaderboardDto;
import com.zarvekule.gamification.dto.LeaderboardEntryDto;

import java.util.List;

public interface LeaderboardService {

    /**
     * XP'ye göre top kullanıcılar
     */
    List<LeaderboardEntryDto> getTopUsersByXp(int limit);

    /**
     * Aldığı beğeniye göre top kullanıcılar
     */
    List<LeaderboardEntryDto> getTopUsersByLikesReceived(int limit);

    /**
     * Ürettiği içerik sayısına göre top kullanıcılar
     */
    List<LeaderboardEntryDto> getTopUsersByContentCount(int limit);

    /**
     * Beğeniye göre top homebrew içerikler
     */
    List<ContentLeaderboardDto> getTopHomebrewsByLikes(int limit);

    /**
     * Seviyeye göre top guild'ler
     */
    List<GuildLeaderboardDto> getTopGuildsByLevel(int limit);
}