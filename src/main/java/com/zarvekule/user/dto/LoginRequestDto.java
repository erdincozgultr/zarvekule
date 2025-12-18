package com.zarvekule.user.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Username boş olamaz")
        String username,

        @NotBlank(message = "Şifre boş olamaz")
        String password
) {}
