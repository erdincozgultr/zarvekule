package com.zarvekule.wiki.mapper;

import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.entity.WikiEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WikiEntryMapper {

    private final UserMapper userMapper;

    public WikiEntryResponse toDto(WikiEntry entity) {
        if (entity == null) {
            return null;
        }

        WikiEntryResponse dto = new WikiEntryResponse();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setContent(entity.getContent());
        dto.setImageUrl(entity.getImageUrl()); // Eklendi
        dto.setCategory(entity.getCategory());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        dto.setLikeCount(entity.getLikeCount()); // Eklendi
        // isLiked serviste set edilecek, burada varsayılan false kalır.

        if (entity.getAuthor() != null) {
            dto.setAuthor(userMapper.toSummaryDto(entity.getAuthor()));
        }

        return dto;
    }

    public List<WikiEntryResponse> toDtoList(List<WikiEntry> entries) {
        if (entries == null) return List.of();
        return entries.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}