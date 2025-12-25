package com.zarvekule.gamification.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GuildDetailDto {
    private Long id;
    private String name;
    private String description;
    private int level;
    private long xp;
    private long xpForNextLevel; // Bir sonraki level için gereken XP
    private int memberCount;
    private GuildMemberDto leader;          // Lonca lideri bilgisi
    private List<GuildMemberDto> members;
    private LocalDateTime createdAt;

    // İsimlendirme değiştirildi - Lombok setter çakışmasını önlemek için
    private boolean currentUserIsMember;    // Mevcut kullanıcı üye mi?
    private boolean currentUserIsLeader;    // Mevcut kullanıcı lider mi?
}