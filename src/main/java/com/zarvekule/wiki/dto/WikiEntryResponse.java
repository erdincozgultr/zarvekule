package com.zarvekule.wiki.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WikiEntryResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String imageUrl; // Resim
    private ContentCategory category;
    private WikiStatus status;
    private UserSummaryDto author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Beğeni Sistemi
    private long likeCount;
    private boolean isLiked;
}