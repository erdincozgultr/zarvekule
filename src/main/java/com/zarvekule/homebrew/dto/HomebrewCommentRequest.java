package com.zarvekule.homebrew.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class HomebrewCommentRequest {

    @NotNull(message = "Homebrew ID gerekli")
    private Long homebrewId;

    @NotBlank(message = "Yorum içeriği boş olamaz")
    @Size(min = 1, max = 1000, message = "Yorum 1-1000 karakter arası olmalı")
    private String content;
}