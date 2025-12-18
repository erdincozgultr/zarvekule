package com.zarvekule.blog.dto;

import com.zarvekule.blog.enums.BlogCategory;
import com.zarvekule.blog.enums.BlogStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class BlogEntryRequest {

    @NotBlank(message = "Başlık boş olamaz.")
    @Size(min = 5, max = 100, message = "Başlık 5 ile 100 karakter arasında olmalıdır.")
    private String title;

    @NotBlank(message = "İçerik boş olamaz.")
    private String content;

    private String thumbnailUrl;

    private BlogStatus status;

    // Yeni Kategori Alanı
    private BlogCategory category;

    private Set<String> tags;

    private String customSlug;

    @Size(max = 70, message = "SEO başlığı 70 karakterden uzun olamaz.")
    private String seoTitle;

    @Size(max = 160, message = "SEO açıklaması 160 karakterden uzun olamaz.")
    private String seoDescription;
}