package com.zarvekule.user.dto;

import lombok.Data;

@Data
public class UserStatsDto {
    private long currentXp;
    private String currentRank;      // PEASANT, ADVENTURER, vb.
    private String rankTitle;        // "Köylü", "Maceracı", vb.
    private int totalBlogs;
    private int totalComments;
    private int totalHomebrews;
    private int totalLikesReceived;
}