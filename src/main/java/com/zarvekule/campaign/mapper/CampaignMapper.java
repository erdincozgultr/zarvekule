package com.zarvekule.campaign.mapper;

import com.zarvekule.campaign.dto.ApplicationResponse;
import com.zarvekule.campaign.dto.CampaignResponse;
import com.zarvekule.campaign.entity.Campaign;
import com.zarvekule.campaign.entity.CampaignApplication;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CampaignMapper {

    private final UserMapper userMapper;

    public CampaignResponse toResponse(Campaign entity) {
        if (entity == null) return null;

        CampaignResponse dto = new CampaignResponse();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());

        dto.setSystem(entity.getSystem());
        dto.setPlatform(entity.getPlatform());
        dto.setFrequency(entity.getFrequency());
        dto.setCity(entity.getCity());
        dto.setDistrict(entity.getDistrict());
        dto.setLevelRange(entity.getLevelRange());

        dto.setMaxPlayers(entity.getMaxPlayers());
        dto.setCurrentPlayers(entity.getCurrentPlayers());
        dto.setStatus(entity.getStatus());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        dto.setDungeonMaster(userMapper.toSummaryDto(entity.getDungeonMaster()));

        return dto;
    }

    public ApplicationResponse toApplicationResponse(CampaignApplication entity) {
        if (entity == null) return null;

        ApplicationResponse dto = new ApplicationResponse();
        dto.setId(entity.getId());
        dto.setPlayer(userMapper.toSummaryDto(entity.getPlayer()));
        dto.setMessage(entity.getMessage());
        dto.setStatus(entity.getStatus());
        dto.setAppliedAt(entity.getAppliedAt());

        dto.setCampaignId(entity.getCampaign().getId());
        dto.setCampaignTitle(entity.getCampaign().getTitle());

        return dto;
    }

    public List<CampaignResponse> toCampaignResponseList(List<Campaign> list) {
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> toApplicationResponseList(List<CampaignApplication> list) {
        return list.stream().map(this::toApplicationResponse).collect(Collectors.toList());
    }
}