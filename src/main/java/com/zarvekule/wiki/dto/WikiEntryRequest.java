package com.zarvekule.wiki.dto;

import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WikiEntryRequest {

    @NotBlank(message = "Başlık boş olamaz.")
    @Size(min = 3, max = 100, message = "Başlık 3-100 karakter arasında olmalıdır.")
    private String title;

    @NotBlank(message = "İçerik boş olamaz.")
    private String content;

    @NotNull(message = "Kategori seçilmelidir.")
    private ContentCategory category;

    private WikiStatus status;

    private String imageUrl;
}