package com.zarvekule.gamification.service;

import com.zarvekule.gamification.dto.*;

import java.util.List;

public interface GuildService {

    /**
     * Tüm guild'leri listele
     */
    List<GuildDto> getAllGuilds(String currentUsername);

    /**
     * Guild detayını getir
     */
    GuildDetailDto getGuildById(Long id, String currentUsername);

    /**
     * Kullanıcının guild'ini getir
     */
    GuildDto getMyGuild(String username);

    /**
     * Yeni guild oluştur
     */
    GuildDto createGuild(String username, GuildCreateRequest request);

    /**
     * Guild'i güncelle (sadece leader)
     */
    GuildDto updateGuild(String username, Long guildId, GuildUpdateRequest request);

    /**
     * Guild'i sil (sadece leader)
     */
    void deleteGuild(String username, Long guildId);

    /**
     * Guild'e katıl
     */
    void joinGuild(String username, Long guildId);

    /**
     * Guild'den ayrıl
     */
    void leaveGuild(String username, Long guildId);

    /**
     * Üyeyi guild'den at (sadece leader)
     */
    void kickMember(String leaderUsername, Long guildId, Long memberId);
}