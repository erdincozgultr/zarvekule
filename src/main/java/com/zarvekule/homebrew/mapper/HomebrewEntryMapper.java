package com.zarvekule.homebrew.mapper;

import com.zarvekule.homebrew.dto.HomebrewEntryListResponse;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HomebrewEntryMapper {

    private final UserMapper userMapper;

    /**
     * Detay görünümü için - content DAHİL
     */
    public HomebrewEntryResponse toResponseDto(HomebrewEntry entity) {
        if (entity == null) return null;

        HomebrewEntryResponse dto = new HomebrewEntryResponse();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());

        dto.setCategory(entity.getCategory());
        dto.setCategoryDisplayName(entity.getCategory() != null ?
                entity.getCategory().getDisplayName() : null);

        dto.setStatus(entity.getStatus());

        // Kategori bazlı detaylı içerik
        dto.setContent(entity.getContent());

        dto.setTags(entity.getTags());

        dto.setViewCount(entity.getViewCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setLiked(false); // Service'de set edilecek

        dto.setForkCount(entity.getForkCount());
        if (entity.getParentEntry() != null) {
            dto.setParentEntryId(entity.getParentEntry().getId());
        }

        if (entity.getAuthor() != null) {
            dto.setAuthor(userMapper.toSummaryDto(entity.getAuthor()));
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setPublishedAt(entity.getPublishedAt());

        return dto;
    }

    /**
     * Liste görünümü için - content HARİÇ (performans)
     */
    public HomebrewEntryListResponse toListDto(HomebrewEntry entity) {
        if (entity == null) return null;

        HomebrewEntryListResponse dto = new HomebrewEntryListResponse();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setDescription(entity.getDescription());
        dto.setImageUrl(entity.getImageUrl());

        dto.setCategory(entity.getCategory());
        dto.setCategoryDisplayName(entity.getCategory() != null ?
                entity.getCategory().getDisplayName() : null);

        dto.setStatus(entity.getStatus());
        dto.setTags(entity.getTags());

        if (entity.getAuthor() != null) {
            dto.setAuthorUsername(entity.getAuthor().getUsername());
            dto.setAuthorDisplayName(entity.getAuthor().getDisplayName() != null ?
                    entity.getAuthor().getDisplayName() : entity.getAuthor().getUsername());
            dto.setAuthorAvatarUrl(entity.getAuthor().getAvatarUrl());
        }

        dto.setViewCount(entity.getViewCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setLiked(false); // Service'de set edilecek

        dto.setForkCount(entity.getForkCount());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPublishedAt(entity.getPublishedAt());

        return dto;
    }

    public List<HomebrewEntryResponse> toResponseDtoList(List<HomebrewEntry> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<HomebrewEntryListResponse> toListDtoList(List<HomebrewEntry> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }
}