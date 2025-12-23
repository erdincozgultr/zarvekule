package com.zarvekule.stats.service;

import com.zarvekule.campaign.repository.CampaignRepository;
import com.zarvekule.community.repository.LikeEntryRepository;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.stats.dto.PublicStatsDto;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final UserRepository userRepository;
    private final HomebrewEntryRepository homebrewRepository;
    private final CampaignRepository campaignRepository;
    private final VenueRepository venueRepository;
    private final LikeEntryRepository likeRepository;

    @Transactional(readOnly = true)
    public PublicStatsDto getPublicStats() {
        PublicStatsDto stats = new PublicStatsDto();

        stats.setTotalUsers(userRepository.count());
        stats.setTotalHomebrews(homebrewRepository.count());
        stats.setTotalCampaigns(campaignRepository.count());
        stats.setTotalVenues(venueRepository.count());
        stats.setTotalLikes(likeRepository.count());

        return stats;
    }
}