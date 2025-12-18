package com.zarvekule.homebrew.dto;

import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class HomebrewEntryResponse {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String excerpt;
    private String imageUrl; // Resim
    private HomebrewCategory category;
    private String rarity;
    private String requiredLevel;
    private Set<String> tags;
    private HomebrewStatus status;
    private UserSummaryDto author;
    private long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    // Beğeni Alanları
    private long likeCount;
    private boolean isLiked;
}