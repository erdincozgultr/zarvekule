package com.zarvekule.gamification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopContributorDto {
    private Long id;
    private String username;
    private String displayName;
    private String avatarUrl;
    private long xpContributed;
    private long contentCount;
}