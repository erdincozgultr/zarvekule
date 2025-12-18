package com.zarvekule.campaign.dto;

import com.zarvekule.campaign.enums.CampaignStatus;
import com.zarvekule.campaign.enums.GameFrequency;
import com.zarvekule.campaign.enums.GameSystem;
import com.zarvekule.campaign.enums.PlayPlatform;
import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CampaignResponse {
    private Long id;
    private String title;
    private String description;

    private GameSystem system;
    private PlayPlatform platform;
    private GameFrequency frequency;

    private String city;
    private String district;
    private String levelRange;

    private int maxPlayers;
    private int currentPlayers;

    private CampaignStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private UserSummaryDto dungeonMaster;
}