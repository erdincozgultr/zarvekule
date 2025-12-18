package com.zarvekule.homebrew.mapper;

import com.zarvekule.homebrew.dto.CollectionDto;
import com.zarvekule.homebrew.entity.HomebrewCollection;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CollectionMapper {

    private final UserMapper userMapper;
    private final HomebrewEntryMapper homebrewEntryMapper;

    public CollectionDto toDto(HomebrewCollection entity) {
        if (entity == null) return null;

        CollectionDto dto = new CollectionDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPublic(entity.isPublic());
        dto.setOwner(userMapper.toSummaryDto(entity.getOwner()));

        dto.setItemCount(entity.getEntries() != null ? entity.getEntries().size() : 0);

        if (entity.getEntries() != null) {
            dto.setEntries(homebrewEntryMapper.toResponseDtoList(
                    entity.getEntries().stream().collect(Collectors.toList())
            ));
        }

        return dto;
    }

    public List<CollectionDto> toDtoList(List<HomebrewCollection> entities) {
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }
}