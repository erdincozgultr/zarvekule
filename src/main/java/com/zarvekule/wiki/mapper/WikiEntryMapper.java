package com.zarvekule.wiki.mapper;

import com.zarvekule.user.dto.UserSummaryDto;
import com.zarvekule.wiki.dto.WikiEntryListResponse;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.entity.WikiEntry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Wiki Entity <-> DTO dönüşüm mapper'ı
 */
@Component
public class WikiEntryMapper {

    /**
     * Detay DTO - metadata ve turkishContent DAHİL
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

        // JSON olarak doğrudan aktar
        dto.setMetadata(entity.getMetadata());
        dto.setTurkishContent(entity.getTurkishContent());

        dto.setSourceKey(entity.getSourceKey());
        dto.setImageUrl(entity.getImageUrl());

        dto.setLikeCount(entity.getLikeCount());
        dto.setViewCount(entity.getViewCount());
        dto.setLiked(false); // Service'de set edilecek

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
     * Liste DTO - metadata ve turkishContent YOK (pagination için hafif)
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

        return dto;
    }

    public List<WikiEntryResponse> toDtoList(List<WikiEntry> entries) {
        if (entries == null) return List.of();
        return entries.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<WikiEntryListResponse> toListDtoList(List<WikiEntry> entries) {
        if (entries == null) return List.of();
        return entries.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }
}
