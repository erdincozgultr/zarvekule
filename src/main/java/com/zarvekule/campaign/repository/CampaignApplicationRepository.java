package com.zarvekule.campaign.repository;

import com.zarvekule.campaign.entity.CampaignApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampaignApplicationRepository extends JpaRepository<CampaignApplication, Long> {

    List<CampaignApplication> findAllByCampaignIdOrderByAppliedAtDesc(Long campaignId);

    Optional<CampaignApplication> findByCampaignIdAndPlayerId(Long campaignId, Long playerId);

    List<CampaignApplication> findAllByPlayerIdOrderByAppliedAtDesc(Long playerId);

    void deleteAllByCampaignId(Long campaignId);
}