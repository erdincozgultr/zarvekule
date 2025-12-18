package com.zarvekule.homebrew.dto;

import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class HomebrewEntryRequest {

    @NotBlank(message = "İsim alanı boş olamaz.")
    @Size(min = 3, max = 100, message = "İsim 3 ile 100 karakter arasında olmalıdır.")
    private String name;

    @NotBlank(message = "Açıklama boş olamaz.")
    private String description;

    @Size(max = 255, message = "Özet 255 karakteri geçemez.")
    private String excerpt;

    @NotNull(message = "Kategori belirtilmelidir.")
    private HomebrewCategory category;

    private HomebrewStatus status;

    private String rarity;
    private String requiredLevel;

    private Set<String> tags;

    private String customSlug;
    private String imageUrl;
}