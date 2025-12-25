package com.zarvekule.gamification.dto;

import lombok.Data;

@Data
public class GuildMemberDto {
    private Long id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private String title;           // Rütbe (Gezgin, Maceracı vb.)
    private boolean leaderStatus;   // Bu üye lider mi? (isLeader yerine - çakışma önleme)
}