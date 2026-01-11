package com.zarvekule.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BlogCollectionRequest {
    @NotBlank(message = "Koleksiyon adı boş olamaz")
    @Size(max = 100, message = "Koleksiyon adı en fazla 100 karakter olabilir")
    private String name;

    @Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;

    private boolean isPublic = true;
}