package com.zarvekule.wiki.dto;

import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * Wiki oluşturma/güncelleme request DTO
 */
@Data
public class WikiEntryRequest {
    
    @NotBlank(message = "Başlık boş olamaz")
    private String title;
    
    @NotNull(message = "Kategori belirtilmeli")
    private ContentCategory category;
    
    /**
     * Orijinal API verisi - JSON olarak gönderilir
     */
    private Map<String, Object> metadata;
    
    /**
     * Türkçe içerik - JSON olarak gönderilir
     */
    private Map<String, Object> turkishContent;
    
    private String sourceKey;
    private String imageUrl;
    private WikiStatus status;
    private String customSlug;
}
