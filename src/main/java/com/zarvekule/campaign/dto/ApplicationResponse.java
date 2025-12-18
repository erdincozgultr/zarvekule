package com.zarvekule.campaign.dto;

import com.zarvekule.campaign.enums.ApplicationStatus;
import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApplicationResponse {
    private Long id;
    private UserSummaryDto player;
    private String message;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;

    private Long campaignId;
    private String campaignTitle;
}