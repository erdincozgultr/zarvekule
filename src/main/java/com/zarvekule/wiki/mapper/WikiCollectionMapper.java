package com.zarvekule.wiki.mapper;

import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.wiki.dto.WikiCollectionDto;
import com.zarvekule.wiki.entity.WikiCollection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WikiCollectionMapper {

    private final UserMapper userMapper;
    private final WikiEntryMapper wikiEntryMapper;

    public WikiCollectionDto toDto(WikiCollection entity) {
        if (entity == null) return null;

        WikiCollectionDto dto = new WikiCollectionDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPublic(entity.isPublic());
        dto.setOwner(userMapper.toSummaryDto(entity.getOwner()));
        dto.setCreatedAt(entity.getCreatedAt());

        dto.setItemCount(entity.getEntries() != null ? entity.getEntries().size() : 0);

        if (entity.getEntries() != null && !entity.getEntries().isEmpty()) {
            dto.setEntries(entity.getEntries().stream()
                    .map(wikiEntryMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public List<WikiCollectionDto> toDtoList(List<WikiCollection> entities) {
        if (entities == null) return List.of();
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}