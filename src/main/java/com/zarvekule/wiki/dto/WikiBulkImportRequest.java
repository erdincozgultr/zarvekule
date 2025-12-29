package com.zarvekule.wiki.dto;

import com.zarvekule.wiki.enums.ContentCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Toplu wiki aktarımı için DTO
 */
@Data
public class WikiBulkImportRequest {
    
    @NotNull(message = "Kategori belirtilmeli")
    private ContentCategory category;
    
    /**
     * Orijinal API verisi - JSON string
     */
    private String metadata;
    
    /**
     * Türkçe içerik - JSON string
     */
    private String turkishContent;
    
    /**
     * Duplicate kontrolü için
     */
    private String sourceKey;
}
