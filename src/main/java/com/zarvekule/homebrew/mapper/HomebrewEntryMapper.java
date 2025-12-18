package com.zarvekule.homebrew.mapper;

import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HomebrewEntryMapper {
    private final UserMapper userMapper;

    public HomebrewEntryResponse toResponseDto(HomebrewEntry entity) {
        if (entity == null) return null;
        HomebrewEntryResponse dto = new HomebrewEntryResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setExcerpt(entity.getExcerpt());
        dto.setImageUrl(entity.getImageUrl());
        dto.setCategory(entity.getCategory());
        dto.setRarity(entity.getRarity());
        dto.setRequiredLevel(entity.getRequiredLevel());
        dto.setTags(entity.getTags());
        dto.setStatus(entity.getStatus());
        dto.setViewCount(entity.getViewCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPublishedAt(entity.getPublishedAt());
        if (entity.getAuthor() != null) dto.setAuthor(userMapper.toSummaryDto(entity.getAuthor()));
        return dto;
    }

    // CollectionMapper'ın hata vermemesi için bu metodun varlığı şarttır:
    public List<HomebrewEntryResponse> toResponseDtoList(List<HomebrewEntry> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(this::toResponseDto).collect(Collectors.toList());
    }
}