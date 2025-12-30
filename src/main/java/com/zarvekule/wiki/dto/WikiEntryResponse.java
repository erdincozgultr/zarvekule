package com.zarvekule.wiki.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Wiki detay görünümü için DTO
 *
 * NOT: metadata artık frontend'e GÖNDERİLMİYOR
 * Sadece turkishContent kullanılıyor
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WikiEntryResponse {

    private Long id;
    private String title;
    private String slug;
    private ContentCategory category;
    private String categoryDisplayName;
    private WikiStatus status;

    /**
     * Türkçe içerik - Frontend SADECE bunu kullanıyor
     * Kategori bazlı JSON verisi
     */
    private Map<String, Object> turkishContent;

    /**
     * NOT: metadata artık response'a dahil EDİLMİYOR
     * Backend'de tutulsa da frontend'e gönderilmiyor
     * Eğer admin paneli gibi bir yerde lazım olursa ayrı bir DTO kullanılabilir
     */
    // private Map<String, Object> metadata; // KALDIRILDI

    private String sourceKey;
    private String imageUrl;

    private long likeCount;
    private long viewCount;
    private boolean liked;

    private UserSummaryDto author;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}