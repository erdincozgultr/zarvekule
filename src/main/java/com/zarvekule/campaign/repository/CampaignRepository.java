package com.zarvekule.campaign.repository;

import com.zarvekule.campaign.entity.Campaign;
import com.zarvekule.campaign.enums.CampaignStatus;
import com.zarvekule.campaign.enums.PlayPlatform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    List<Campaign> findAllByStatusOrderByCreatedAtDesc(CampaignStatus status);

    List<Campaign> findAllByDungeonMaster_IdOrderByCreatedAtDesc(Long dmId);

    List<Campaign> findAllByOrderByCreatedAtDesc(); //

    long countByStatus(CampaignStatus status); //

    @Query("SELECT c FROM Campaign c WHERE " +
            "(c.status = 'OPEN') AND " +
            "(:platform IS NULL OR c.platform = :platform) AND " +
            "(:city IS NULL OR c.city = :city) AND " +
            "(:system IS NULL OR c.system = :system)")
    List<Campaign> searchCampaigns(@Param("platform") PlayPlatform platform,
                                   @Param("city") String city,
                                   @Param("system") Object system);


    long countByDungeonMaster_IdInAndCreatedAtAfter(List<Long> dmIds, LocalDateTime createdAt);

    long countByDungeonMaster_IdAndCreatedAtAfter(Long dmId, LocalDateTime createdAt);
}