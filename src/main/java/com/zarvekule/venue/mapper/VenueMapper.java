package com.zarvekule.venue.mapper;

import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.venue.dto.VenueResponse;
import com.zarvekule.venue.entity.Venue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VenueMapper {

    private final UserMapper userMapper;

    public VenueResponse toResponse(Venue entity) {
        return toResponse(entity, null);
    }

    public VenueResponse toResponse(Venue entity, Double distanceKm) {
        if (entity == null) return null;

        VenueResponse dto = new VenueResponse();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setType(entity.getType());
        dto.setAddress(entity.getAddress());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setPhone(entity.getPhone());
        dto.setWebsite(entity.getWebsite());
        dto.setInstagramHandle(entity.getInstagramHandle());
        dto.setStatus(entity.getStatus());
        dto.setAverageRating(entity.getAverageRating());
        dto.setReviewCount(entity.getReviewCount());

        if (entity.getOwner() != null) {
            dto.setOwner(userMapper.toSummaryDto(entity.getOwner()));
        }

        dto.setDistanceKm(distanceKm);

        return dto;
    }
}