package com.zarvekule.homebrew.mapper;

import com.zarvekule.homebrew.dto.HomebrewEntryListResponse;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        dto.setCategoryData(extractCategoryData(entity));
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

    private Map<String, Object> extractCategoryData(HomebrewEntry entry) {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> contentData = entry.getContent();

        if (contentData == null || contentData.isEmpty()) {
            return data;
        }

        try {
            switch (entry.getCategory()) {
                case SPELLS:
                    if (contentData.containsKey("level")) {
                        data.put("level", contentData.get("level"));
                    }
                    if (contentData.containsKey("school")) {
                        data.put("school", contentData.get("school"));
                    }
                    break;

                case MONSTERS:
                    if (contentData.containsKey("type")) {
                        data.put("type", contentData.get("type"));
                    }
                    if (contentData.containsKey("challenge_rating")) {
                        data.put("challenge_rating", contentData.get("challenge_rating"));
                    }
                    break;

                case MAGIC_ITEM:
                    if (contentData.containsKey("rarity")) {
                        data.put("rarity", contentData.get("rarity"));
                    }
                    if (contentData.containsKey("attunement")) {
                        data.put("attunement", contentData.get("attunement"));
                    }
                    break;

                case WEAPON:
                    if (contentData.containsKey("damage")) {
                        data.put("damage", contentData.get("damage"));
                    }
                    break;

                case ARMOR:
                    if (contentData.containsKey("armor_class")) {
                        data.put("armor_class", contentData.get("armor_class"));
                    }
                    if (contentData.containsKey("type")) {
                        data.put("type", contentData.get("type"));
                    }
                    break;

                case CLASSES:
                    if (contentData.containsKey("hit_die")) {
                        data.put("hit_die", contentData.get("hit_die"));
                    }
                    if (contentData.containsKey("primary_ability")) {
                        data.put("primary_ability", contentData.get("primary_ability"));
                    }
                    break;

                case RACES:
                    if (contentData.containsKey("size")) {
                        data.put("size", contentData.get("size"));
                    }
                    if (contentData.containsKey("speed")) {
                        data.put("speed", contentData.get("speed"));
                    }
                    break;

                case FEATS:
                    if (contentData.containsKey("prerequisite")) {
                        data.put("prerequisite", contentData.get("prerequisite"));
                    }
                    break;

                case BACKGROUND:
                    if (contentData.containsKey("skill_proficiencies")) {
                        data.put("skill_proficiencies", contentData.get("skill_proficiencies"));
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {

        }

        return data;
    }
}