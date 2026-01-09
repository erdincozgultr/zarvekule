package com.zarvekule.homebrew.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HomebrewCommentRequest {

    @NotNull(message = "Homebrew ID boş olamaz")
    private Long homebrewId;

    @NotBlank(message = "Yorum içeriği boş olamaz")
    private String content;
}