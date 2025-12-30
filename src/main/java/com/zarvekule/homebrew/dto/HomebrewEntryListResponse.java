package com.zarvekule.homebrew.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HomebrewEntryListResponse {

    private Long id;
    private String name;
    private String slug;
    private String description; // Kısa açıklama
    private String imageUrl;

    private HomebrewCategory category;
    private String categoryDisplayName;

    private HomebrewStatus status;

    private Set<String> tags;

    private String authorUsername;
    private String authorDisplayName;
    private String authorAvatarUrl;

    private long viewCount;
    private long likeCount;
    private boolean liked;

    private long forkCount;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
}