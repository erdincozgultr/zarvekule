package com.zarvekule.wiki.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WikiCommentRequest {

    @NotNull(message = "Wiki ID boş olamaz")
    private Long wikiId;

    @NotBlank(message = "Yorum içeriği boş olamaz")
    private String content;
}