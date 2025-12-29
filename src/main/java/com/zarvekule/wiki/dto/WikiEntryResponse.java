package com.zarvekule.wiki.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Wiki detay görünümü için tam DTO
 * metadata ve turkishContent DAHİL
 */
@Data
public class WikiEntryResponse {
    
    private Long id;
    private String title;
    private String slug;
    private ContentCategory category;
    private String categoryDisplayName;
    private WikiStatus status;
    
    /**
     * Orijinal API verisi - Frontend parse edecek
     */
    private Map<String, Object> metadata;
    
    /**
     * Türkçe içerik - Frontend parse edecek
     */
    private Map<String, Object> turkishContent;
    
    private String sourceKey;
    private String imageUrl;
    
    private long likeCount;
    private long viewCount;
    private boolean liked;
    
    private UserSummaryDto author;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
