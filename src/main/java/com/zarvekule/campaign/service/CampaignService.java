package com.zarvekule.campaign.service;

import com.zarvekule.campaign.dto.ApplicationRequest;
import com.zarvekule.campaign.dto.ApplicationResponse;
import com.zarvekule.campaign.dto.CampaignRequest;
import com.zarvekule.campaign.dto.CampaignResponse;


import java.util.List;

public interface CampaignService {

    CampaignResponse create(String username, CampaignRequest request);

    void applyToCampaign(String username, Long campaignId, ApplicationRequest request);

    void manageApplication(String dmUsername, Long applicationId, boolean isAccepted);

    List<ApplicationResponse> getApplicationsForCampaign(String dmUsername, Long campaignId);

    List<ApplicationResponse> getMyApplications(String username);

    List<CampaignResponse> getAllCampaigns();

    long countOpenCampaigns();

    void deleteCampaign(String username, Long campaignId);
}