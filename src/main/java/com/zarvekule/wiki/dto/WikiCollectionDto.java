package com.zarvekule.wiki.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WikiCollectionDto {
    private Long id;
    private String name;
    private String description;
    private UserSummaryDto owner;
    private boolean isPublic;
    private int itemCount;
    private List<WikiEntryResponse> entries;
    private LocalDateTime createdAt;
}