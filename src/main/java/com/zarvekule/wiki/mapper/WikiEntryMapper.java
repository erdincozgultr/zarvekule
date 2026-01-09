package com.zarvekule.wiki.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.wiki.dto.WikiEntryListResponse;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.entity.WikiEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WikiEntryMapper {

    private final ObjectMapper objectMapper;

    /**
     * Detay DTO - SADECE turkishContent dahil
     */
    public WikiEntryResponse toDto(WikiEntry entity) {
        if (entity == null) return null;

        WikiEntryResponse dto = new WikiEntryResponse();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setCategory(entity.getCategory());
        dto.setCategoryDisplayName(entity.getCategory() != null ?
                entity.getCategory().getDisplayName() : null);
        dto.setStatus(entity.getStatus());
        dto.setTurkishContent(entity.getTurkishContent());
        dto.setSourceKey(entity.getSourceKey());
        dto.setImageUrl(entity.getImageUrl());
        dto.setLikeCount(entity.getLikeCount());
        dto.setViewCount(entity.getViewCount());
        dto.setLiked(false);

        if (entity.getAuthor() != null) {
            dto.setAuthor(new UserSummaryDto(
                    entity.getAuthor().getUsername(),
                    entity.getAuthor().getDisplayName() != null ?
                            entity.getAuthor().getDisplayName() : entity.getAuthor().getUsername(),
                    entity.getAuthor().getAvatarUrl(),
                    entity.getAuthor().getTitle() != null ?
                            entity.getAuthor().getTitle() : "Gezgin"
            ));
        }

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    /**
     * Liste DTO - ✅ categoryData eklendi
     */
    public WikiEntryListResponse toListDto(WikiEntry entity) {
        if (entity == null) return null;

        WikiEntryListResponse dto = new WikiEntryListResponse();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setCategory(entity.getCategory());
        dto.setCategoryDisplayName(entity.getCategory() != null ?
                entity.getCategory().getDisplayName() : null);
        dto.setImageUrl(entity.getImageUrl());
        dto.setLikeCount(entity.getLikeCount());
        dto.setViewCount(entity.getViewCount());
        dto.setLiked(false);

        if (entity.getAuthor() != null) {
            dto.setAuthorUsername(entity.getAuthor().getUsername());
        }

        // ✅ YENİ: Kategori detaylarını ekle
        dto.setCategoryData(extractCategoryData(entity));

        return dto;
    }

    private Map<String, Object> extractCategoryData(WikiEntry entry) {
        Map<String, Object> data = new HashMap<>();

        Map<String, Object> turkishContent = entry.getTurkishContent();

        if (turkishContent == null || turkishContent.isEmpty()) {
            return data;
        }

        try {
            switch (entry.getCategory()) {
                case SPELLS:
                    if (turkishContent.containsKey("level")) {
                        data.put("level", turkishContent.get("level"));
                    }
                    if (turkishContent.containsKey("school")) {
                        data.put("school", turkishContent.get("school"));
                    }
                    break;

                case MONSTERS:
                    if (turkishContent.containsKey("type")) {
                        data.put("type", turkishContent.get("type"));
                    }
                    if (turkishContent.containsKey("challenge_rating")) {
                        data.put("challenge_rating", turkishContent.get("challenge_rating"));
                    }
                    break;

                case MAGIC_ITEM:
                    if (turkishContent.containsKey("rarity")) {
                        data.put("rarity", turkishContent.get("rarity"));
                    }
                    if (turkishContent.containsKey("attunement")) {
                        data.put("attunement", turkishContent.get("attunement"));
                    }
                    break;

                case WEAPON:
                    if (turkishContent.containsKey("damage")) {
                        data.put("damage", turkishContent.get("damage"));
                    }
                    break;

                case ARMOR:
                    if (turkishContent.containsKey("armor_class")) {
                        data.put("armor_class", turkishContent.get("armor_class"));
                    }
                    if (turkishContent.containsKey("type")) {
                        data.put("type", turkishContent.get("type"));
                    }
                    break;

                case CLASSES:
                    if (turkishContent.containsKey("hit_die")) {
                        data.put("hit_die", turkishContent.get("hit_die"));
                    }
                    if (turkishContent.containsKey("primary_ability")) {
                        data.put("primary_ability", turkishContent.get("primary_ability"));
                    }
                    break;

                case RACES:
                    if (turkishContent.containsKey("size")) {
                        data.put("size", turkishContent.get("size"));
                    }
                    if (turkishContent.containsKey("speed")) {
                        data.put("speed", turkishContent.get("speed"));
                    }
                    break;

                case FEATS:
                    if (turkishContent.containsKey("prerequisite")) {
                        data.put("prerequisite", turkishContent.get("prerequisite"));
                    }
                    break;

                case BACKGROUND:
                    if (turkishContent.containsKey("skill_proficiencies")) {
                        data.put("skill_proficiencies", turkishContent.get("skill_proficiencies"));
                    }
                    break;

                default:
                    break;
            }
        } catch (Exception e) {
            log.warn("Failed to extract category data for entry: {}", entry.getId(), e);
        }

        return data;
    }
}