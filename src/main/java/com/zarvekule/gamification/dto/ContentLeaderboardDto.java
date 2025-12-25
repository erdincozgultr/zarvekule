package com.zarvekule.gamification.dto;

import lombok.Data;

@Data
public class ContentLeaderboardDto {
    private int rank;
    private Long contentId;
    private String title;
    private String slug;
    private String authorUsername;
    private long likeCount;
}