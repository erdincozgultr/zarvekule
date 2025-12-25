package com.zarvekule.gamification.dto;

import jakarta.validation.constraints.Size;

public record GuildUpdateRequest(
        @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
        String description
) {}