package com.zarvekule.gamification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record GuildCreateRequest(
        @NotBlank(message = "Lonca adı boş olamaz")
        @Size(min = 3, max = 50, message = "Lonca adı 3-50 karakter arasında olmalıdır")
        String name,

        @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
        String description
) {}