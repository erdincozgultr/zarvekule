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
    private GuildMemberDto leader;
    private List<GuildMemberDto> members;
    private LocalDateTime createdAt;
    private boolean isMember;
    private boolean isLeader;
}