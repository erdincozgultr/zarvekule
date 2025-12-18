package com.zarvekule.homebrew.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CollectionRequest {
    @NotBlank
    private String name;
    private String description;
    private boolean isPublic = true;
}