package com.zarvekule.security;

import com.zarvekule.campaign.entity.Campaign;
import com.zarvekule.campaign.repository.CampaignRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("campaignAuthorization")
@RequiredArgsConstructor
public class CampaignAuthorizationManager {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public boolean isDm(Long campaignId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return false;

        String username = auth.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        if (currentUser == null) return false;

        Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
        if (campaign == null) return false;

        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

        return isAdmin || campaign.getDungeonMaster().getId().equals(currentUser.getId());
    }
}