package com.zarvekule.blog.mapper;

import com.zarvekule.blog.dto.BlogEntryResponse;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BlogEntryMapper {

    private final UserMapper userMapper;

    public BlogEntrySummary toSummaryDto(BlogEntry entity) {
        if (entity == null) return null;

        BlogEntrySummary dto = new BlogEntrySummary();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setThumbnailUrl(entity.getThumbnailUrl());
        dto.setStatus(entity.getStatus());

        // Yeni maplemeler
        dto.setCategory(entity.getCategory());
        dto.setReadingTime(entity.getReadingTime());

        dto.setViewCount(entity.getViewCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setPublishedAt(entity.getPublishedAt());

        dto.setAuthor(userMapper.toSummaryDto(entity.getAuthor()));

        return dto;
    }

    public BlogEntryResponse toResponseDto(BlogEntry entity) {
        if (entity == null) return null;

        BlogEntryResponse dto = new BlogEntryResponse();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setSlug(entity.getSlug());
        dto.setContent(entity.getContent());
        dto.setThumbnailUrl(entity.getThumbnailUrl());
        dto.setStatus(entity.getStatus());

        // Yeni maplemeler
        dto.setCategory(entity.getCategory());
        dto.setReadingTime(entity.getReadingTime());
        dto.setSeoTitle(entity.getSeoTitle());
        dto.setSeoDescription(entity.getSeoDescription());

        dto.setViewCount(entity.getViewCount());
        dto.setLikeCount(entity.getLikeCount());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setPublishedAt(entity.getPublishedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setTags(entity.getTags());

        dto.setAuthor(userMapper.toSummaryDto(entity.getAuthor()));

        return dto;
    }
}