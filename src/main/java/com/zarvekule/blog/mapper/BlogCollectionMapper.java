package com.zarvekule.blog.mapper;

import com.zarvekule.blog.dto.BlogCollectionDto;
import com.zarvekule.blog.entity.BlogCollection;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BlogCollectionMapper {

    private final UserMapper userMapper;
    private final BlogEntryMapper blogEntryMapper;

    public BlogCollectionDto toDto(BlogCollection entity) {
        if (entity == null) return null;

        BlogCollectionDto dto = new BlogCollectionDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPublic(entity.isPublic());
        dto.setOwner(userMapper.toSummaryDto(entity.getOwner()));
        dto.setCreatedAt(entity.getCreatedAt());

        dto.setItemCount(entity.getEntries() != null ? entity.getEntries().size() : 0);

        if (entity.getEntries() != null && !entity.getEntries().isEmpty()) {
            dto.setEntries(entity.getEntries().stream()
                    .map(blogEntryMapper::toSummaryDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<BlogCollectionDto> toDtoList(List<BlogCollection> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}