package com.zarvekule.gamification.dto;

import lombok.Data;

@Data
public class GuildLeaderboardDto {
    private int rank;
    private Long guildId;
    private String guildName;
    private int level;
    private long xp;
    private int memberCount;
    private String leaderUsername;
    private int questsCompleted;
}