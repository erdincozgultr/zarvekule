package com.zarvekule.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicStatsDto {
    private long totalUsers;
    private long totalHomebrews;
    private long totalCampaigns;
    private long totalVenues;
    private long totalGuilds;
    private long totalLikes;
}