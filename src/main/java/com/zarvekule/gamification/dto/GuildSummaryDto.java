package com.zarvekule.gamification.dto;

import lombok.Data;

@Data
public class GuildSummaryDto {
    private Long id;
    private String name;
    private String description;
    private int level;
    private long xp;
    private int memberCount;
}