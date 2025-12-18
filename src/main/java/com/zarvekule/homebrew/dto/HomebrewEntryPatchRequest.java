package com.zarvekule.homebrew.dto;

import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import lombok.Data;

import java.util.Set;

@Data
public class HomebrewEntryPatchRequest {

    private String name;
    private String description;
    private String excerpt;

    private HomebrewCategory category;
    private HomebrewStatus status;

    private String rarity;
    private String requiredLevel;

    private Set<String> tags;
    private String customSlug;
    private String imageUrl;
}