package com.zarvekule.homebrew.dto;

import com.zarvekule.homebrew.enums.HomebrewCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class HomebrewEntryRequest {

    @NotBlank(message = "İsim alanı boş olamaz.")
    @Size(min = 2, max = 100, message = "İsim 2 ile 100 karakter arasında olmalıdır.")
    private String name;

    @Size(max = 1000, message = "Açıklama 1000 karakteri geçemez.")
    private String description; // Kısa açıklama (liste görünümü için)

    @NotNull(message = "Kategori belirtilmelidir.")
    private HomebrewCategory category;

    @NotNull(message = "İçerik boş olamaz.")
    private Map<String, Object> content;

    private Set<String> tags;

    private String customSlug;

    private String imageUrl;
}