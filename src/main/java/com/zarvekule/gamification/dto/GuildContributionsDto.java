package com.zarvekule.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuildContributionsDto {
    private long homebrewsCount;
    private long blogsCount;
    private long campaignsCount;
    private List<Object> recentHomebrews;
    private List<Object> recentBlogs;
    private List<TopContributorDto> topContributors;
}