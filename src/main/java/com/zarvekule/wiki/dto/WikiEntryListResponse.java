package com.zarvekule.wiki.dto;

import com.zarvekule.wiki.enums.ContentCategory;
import lombok.Data;

import java.util.Map;

/**
 * Wiki liste görünümü için hafif DTO
 * Pagination performansı için metadata/turkishContent dahil DEĞİL
 */
@Data
public class WikiEntryListResponse {
    
    private Long id;
    private String title;
    private String slug;
    private ContentCategory category;
    private String categoryDisplayName;
    private String imageUrl;
    private long likeCount;
    private long viewCount;
    private boolean liked;
    private String authorUsername;
    private Map<String, Object> categoryData;
}
