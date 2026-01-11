package com.zarvekule.campaign.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.campaign.entity.Campaign;
import com.zarvekule.campaign.repository.CampaignRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.moderation.dto.ModerationAction;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Campaign (Partibul) Moderasyon Service
 *
 * Moderatör yetkileri:
 * - Campaign silme
 *
 * NOT: Campaign'ler direkt yayınlanır, moderatör sadece silebilir
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignModerationService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /**
     * Campaign'i sil
     */
    @Transactional
    public void deleteCampaign(String moderatorUsername, Long campaignId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ApiException("Oyun ilanı bulunamadı", HttpStatus.NOT_FOUND));

        String campaignTitle = campaign.getTitle();
        User dm = campaign.getDungeonMaster();

        // Audit log (silmeden önce kaydet)
        String details = String.format("Sebep: %s, Başlık: %s, DM: %s, Status: %s",
                action.getReason(),
                campaignTitle,
                dm.getUsername(),
                campaign.getStatus());

        auditService.logAction(
                moderatorUsername,
                AuditAction.CAMPAIGN_DELETED,
                "CAMPAIGN",
                campaignId,
                details
        );

        // DM'e bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Oyun ilanınız '" + campaignTitle + "' silindi. Sebep: " + action.getReason();

        notificationService.createNotification(
                dm,
                "🗑️ Oyun İlanı Silindi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/campaigns/list"
        );

        // Campaign'i sil
        campaignRepository.delete(campaign);

        log.info("Campaign deleted - ID: {}, Title: {}, Moderator: {}, Reason: {}",
                campaignId, campaignTitle, moderatorUsername, action.getReason());
    }
}