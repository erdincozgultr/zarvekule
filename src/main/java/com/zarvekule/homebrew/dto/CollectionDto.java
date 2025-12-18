package com.zarvekule.homebrew.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;
import java.util.List;

@Data
public class CollectionDto {
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private UserSummaryDto owner;
    private int itemCount;

    private List<HomebrewEntryResponse> entries;
}