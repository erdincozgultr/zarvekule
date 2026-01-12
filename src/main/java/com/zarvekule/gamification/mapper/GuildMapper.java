package com.zarvekule.gamification.mapper;

import com.zarvekule.gamification.dto.GuildDto;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Guild Mapper
 * Guild entity'sini GuildDto'ya çevirir
 * NOT: Moderasyon için basit mapping, currentUserIsMember false olarak set edilir
 */
@Component
@RequiredArgsConstructor
public class GuildMapper {

    private final UserMapper userMapper;

    /**
     * Guild'i DTO'ya çevir
     * Moderasyon için kullanılır, currentUserIsMember false
     */
    public GuildDto toDto(Guild entity) {
        if (entity == null) return null;

        GuildDto dto = new GuildDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setLevel(entity.getLevel());
        dto.setXp(entity.getXp());
        dto.setBannerUrl(entity.getBannerUrl());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setCreatedAt(entity.getCreatedAt());

        dto.setMemberCount(entity.getMembers() != null ? entity.getMembers().size() : 0);
        dto.setLeader(entity.getLeader() != null ? userMapper.toSummaryDto(entity.getLeader()) : null);

        // Moderasyon için currentUserIsMember false
        dto.setCurrentUserIsMember(false);

        return dto;
    }
}