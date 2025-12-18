package com.zarvekule.marketplace.mapper;

import com.zarvekule.marketplace.dto.ListingResponse;
import com.zarvekule.marketplace.entity.MarketplaceListing;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MarketplaceMapper {

    private final UserMapper userMapper;

    public ListingResponse toResponse(MarketplaceListing entity) {
        if (entity == null) return null;

        ListingResponse dto = new ListingResponse();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setCategory(entity.getCategory());
        dto.setCondition(entity.getCondition());
        dto.setContactInfo(entity.getContactInfo());
        dto.setImageUrl(entity.getImageUrl());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setExpiresAt(entity.getExpiresAt());

        dto.setSeller(userMapper.toSummaryDto(entity.getSeller()));

        return dto;
    }

    public List<ListingResponse> toResponseList(List<MarketplaceListing> list) {
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }
}