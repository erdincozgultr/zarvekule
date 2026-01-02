package com.zarvekule.campaign.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.campaign.dto.ApplicationRequest;
import com.zarvekule.campaign.dto.ApplicationResponse;
import com.zarvekule.campaign.dto.CampaignRequest;
import com.zarvekule.campaign.dto.CampaignResponse;
import com.zarvekule.campaign.entity.Campaign;
import com.zarvekule.campaign.entity.CampaignApplication;
import com.zarvekule.campaign.enums.ApplicationStatus;
import com.zarvekule.campaign.enums.CampaignStatus;
import com.zarvekule.campaign.mapper.CampaignMapper;
import com.zarvekule.campaign.repository.CampaignApplicationRepository;
import com.zarvekule.campaign.repository.CampaignRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.Role;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.enums.ERole;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CampaignApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final CampaignMapper campaignMapper;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final GamificationService gamificationService;

    @Override
    @Transactional
    public CampaignResponse create(String username, CampaignRequest request) {
        User dm = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        Campaign campaign = new Campaign();
        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setSystem(request.getSystem());
        campaign.setPlatform(request.getPlatform());
        campaign.setFrequency(request.getFrequency());
        campaign.setCity(request.getCity());
        campaign.setDistrict(request.getDistrict());
        campaign.setVirtualTableLink(request.getVirtualTableLink());
        campaign.setMaxPlayers(request.getMaxPlayers());
        campaign.setLevelRange(request.getLevelRange());

        campaign.setDungeonMaster(dm);
        campaign.setStatus(CampaignStatus.OPEN);
        campaign.setCreatedAt(LocalDateTime.now());

        Campaign saved = campaignRepository.save(campaign);

        // ✅ YENİ: Campaign oluşturuldu, XP ver
        gamificationService.processAction(dm, ActionType.CREATE_CAMPAIGN);

        return campaignMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CampaignResponse> getAllCampaigns() {
        return campaignMapper.toCampaignResponseList( //
                campaignRepository.findAllByOrderByCreatedAtDesc()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long countOpenCampaigns() {
        return campaignRepository.countByStatus(CampaignStatus.OPEN);
    }

    @Override
    @Transactional
    public void applyToCampaign(String username, Long campaignId, ApplicationRequest request) {
        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ApiException("İlan bulunamadı.", HttpStatus.NOT_FOUND));

        if (campaign.getStatus() != CampaignStatus.OPEN) {
            throw new ApiException("Bu ilan başvurulara kapalı.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.equals(campaign.getDungeonMaster().getId(), player.getId())) {
            throw new ApiException("Kendi oyununuza başvuramazsınız.", HttpStatus.BAD_REQUEST);
        }
        if (applicationRepository.findByCampaignIdAndPlayerId(campaignId, player.getId()).isPresent()) {
            throw new ApiException("Zaten başvurunuz var.", HttpStatus.CONFLICT);
        }

        CampaignApplication app = new CampaignApplication();
        app.setCampaign(campaign);
        app.setPlayer(player);
        app.setMessage(request.getMessage());
        app.setStatus(ApplicationStatus.PENDING);

        applicationRepository.save(app);

        notificationService.createNotification(
                campaign.getDungeonMaster(),
                "Yeni Oyuncu Başvurusu",
                player.getUsername() + ", '" + campaign.getTitle() + "' oyununa katılmak istiyor.",
                NotificationType.CAMPAIGN_STATUS,
                null
        );

        // ✅ YENİ: Kampanyaya başvuru yaptı, XP ver
        gamificationService.processAction(player, ActionType.JOIN_CAMPAIGN);
    }

    @Override
    @Transactional
    public void manageApplication(String dmUsername, Long applicationId, boolean isAccepted) {
        CampaignApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ApiException("Başvuru bulunamadı.", HttpStatus.NOT_FOUND));

        Campaign campaign = app.getCampaign();

        if (!campaign.getDungeonMaster().getUsername().equals(dmUsername)) {
            throw new ApiException("Bu başvuruyu yönetme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        if (isAccepted) {
            if (campaign.getCurrentPlayers() >= campaign.getMaxPlayers()) {
                throw new ApiException("Kontenjan dolu!", HttpStatus.BAD_REQUEST);
            }
            app.setStatus(ApplicationStatus.ACCEPTED);
            campaign.setCurrentPlayers(campaign.getCurrentPlayers() + 1);

            if (campaign.getCurrentPlayers() >= campaign.getMaxPlayers()) {
                campaign.setStatus(CampaignStatus.FULL);
            }
            campaignRepository.save(campaign);
            notificationService.createNotification(
                    app.getPlayer(),
                    "Başvurun Kabul Edildi! ⚔️",
                    "'" + campaign.getTitle() + "' oyununa kabul edildin. Hazırlan!",
                    NotificationType.CAMPAIGN_STATUS,
                    "/campaigns/" + campaign.getId()
            );
        } else {
            app.setStatus(ApplicationStatus.REJECTED);

            notificationService.createNotification(
                    app.getPlayer(),
                    "Başvurun Reddedildi",
                    "Maalesef '" + campaign.getTitle() + "' oyunu için başvurun kabul edilmedi.",
                    NotificationType.CAMPAIGN_STATUS,
                    "/campaigns/list"
            );
        }

        applicationRepository.save(app);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsForCampaign(String dmUsername, Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ApiException("İlan bulunamadı.", HttpStatus.NOT_FOUND));

        if (!campaign.getDungeonMaster().getUsername().equals(dmUsername)) {
            throw new ApiException("Yetkisiz işlem.", HttpStatus.FORBIDDEN);
        }

        return campaignMapper.toApplicationResponseList(
                applicationRepository.findAllByCampaignIdOrderByAppliedAtDesc(campaignId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getMyApplications(String username) {
        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        return campaignMapper.toApplicationResponseList(
                applicationRepository.findAllByPlayerIdOrderByAppliedAtDesc(player.getId()));
    }

    @Override
    @Transactional
    public void deleteCampaign(String username, Long campaignId) {

        User actor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ApiException("İlan bulunamadı.", HttpStatus.NOT_FOUND));

        boolean isOwner = campaign.getDungeonMaster().getId().equals(actor.getId());
        boolean hasAuthority = false;

        Set<Role> roles = actor.getRoles();
        if (roles != null) {
            hasAuthority = roles.stream()
                    .anyMatch(r -> r.getName() == ERole.ROLE_ADMIN || r.getName() == ERole.ROLE_MODERATOR);
        }

        if (!isOwner && !hasAuthority) {
            auditService.logAction(username, AuditAction.SENSITIVE_DATA_ACCESS, "CAMPAIGN", campaignId, "Yetkisiz silme denemesi");
            throw new ApiException("Bu ilanı silmeye yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        if (!isOwner) {
            notificationService.createNotification(
                    campaign.getDungeonMaster(),
                    "İlanınız Silindi",
                    "'" + campaign.getTitle() + "' başlıklı ilanınız bir yönetici tarafından kaldırıldı.",
                    NotificationType.SYSTEM,
                    null
            );
        }

        List<CampaignApplication> applications = applicationRepository.findAllByCampaignIdOrderByAppliedAtDesc(campaignId);
        for (CampaignApplication app : applications) {

            if (!app.getPlayer().getId().equals(actor.getId())) {
                notificationService.createNotification(
                        app.getPlayer(),
                        "İlan İptal Edildi",
                        "Başvurduğunuz '" + campaign.getTitle() + "' başlıklı oyun iptal edildi/silindi.",
                        NotificationType.CAMPAIGN_STATUS,
                        "/campaigns/list"
                );
            }
        }
        auditService.logAction(
                username,
                AuditAction.CAMPAIGN_DELETE,
                "CAMPAIGN",
                campaignId,
                "Campaign Title: " + campaign.getTitle() + " | Owner: " + campaign.getDungeonMaster().getUsername()
        );

        applicationRepository.deleteAllByCampaignId(campaignId);
        campaignRepository.delete(campaign);
    }
}