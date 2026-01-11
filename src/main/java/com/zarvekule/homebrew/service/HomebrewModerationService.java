package com.zarvekule.homebrew.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
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
 * Homebrew Moderasyon Service
 *
 * Moderatör yetkileri:
 * - Homebrew onaylama/reddetme
 * - Drafta alma
 * - Metadata düzenleme
 * - Yorum silme
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HomebrewModerationService {

    private final HomebrewEntryRepository homebrewRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /**
     * Homebrew'ı onayla (PENDING → PUBLISHED)
     */
    @Transactional
    public void approveHomebrew(String moderatorUsername, Long homebrewId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        HomebrewEntry homebrew = homebrewRepository.findById(homebrewId)
                .orElseThrow(() -> new ApiException("Homebrew bulunamadı", HttpStatus.NOT_FOUND));

        if (homebrew.getStatus() != HomebrewStatus.PENDING_APPROVAL) {
            throw new ApiException("Sadece beklemedeki içerikler onaylanabilir", HttpStatus.BAD_REQUEST);
        }

        // Status güncelle
        homebrew.setStatus(HomebrewStatus.PUBLISHED);
        homebrewRepository.save(homebrew);

        // Audit log
        auditService.logAction(
                moderatorUsername,
                AuditAction.HOMEBREW_APPROVED,
                "HOMEBREW",
                homebrewId,
                "Sebep: " + action.getReason()
        );

        // Kullanıcıya bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Homebrew içeriğin '" + homebrew.getName() + "' onaylandı ve yayınlandı!";

        notificationService.createNotification(
                homebrew.getAuthor(),
                "✅ Homebrew Onaylandı",
                notificationMessage,
                NotificationType.CONTENT_APPROVED,
                "/homebrew/" + homebrew.getSlug()
        );

        log.info("Homebrew approved - ID: {}, Moderator: {}, Reason: {}",
                homebrewId, moderatorUsername, action.getReason());
    }

    /**
     * Homebrew'ı reddet (PENDING → DRAFT)
     */
    @Transactional
    public void rejectHomebrew(String moderatorUsername, Long homebrewId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        HomebrewEntry homebrew = homebrewRepository.findById(homebrewId)
                .orElseThrow(() -> new ApiException("Homebrew bulunamadı", HttpStatus.NOT_FOUND));

        if (homebrew.getStatus() != HomebrewStatus.PENDING_APPROVAL) {
            throw new ApiException("Sadece beklemedeki içerikler reddedilebilir", HttpStatus.BAD_REQUEST);
        }

        // Status güncelle
        homebrew.setStatus(HomebrewStatus.DRAFT);
        homebrewRepository.save(homebrew);

        // Audit log
        auditService.logAction(
                moderatorUsername,
                AuditAction.HOMEBREW_REJECTED,
                "HOMEBREW",
                homebrewId,
                "Sebep: " + action.getReason()
        );

        // Kullanıcıya bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Homebrew içeriğin '" + homebrew.getName() + "' reddedildi. Sebep: " + action.getReason();

        notificationService.createNotification(
                homebrew.getAuthor(),
                "❌ Homebrew Reddedildi",
                notificationMessage,
                NotificationType.CONTENT_REJECTED,
                "/homebrews/me"
        );

        log.info("Homebrew rejected - ID: {}, Moderator: {}, Reason: {}",
                homebrewId, moderatorUsername, action.getReason());
    }

    /**
     * Homebrew'ı drafta al (PUBLISHED → DRAFT)
     */
    @Transactional
    public void moveToDraft(String moderatorUsername, Long homebrewId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        HomebrewEntry homebrew = homebrewRepository.findById(homebrewId)
                .orElseThrow(() -> new ApiException("Homebrew bulunamadı", HttpStatus.NOT_FOUND));

        if (homebrew.getStatus() != HomebrewStatus.PUBLISHED) {
            throw new ApiException("Sadece yayındaki içerikler drafta alınabilir", HttpStatus.BAD_REQUEST);
        }

        // Status güncelle
        homebrew.setStatus(HomebrewStatus.DRAFT);
        homebrewRepository.save(homebrew);

        // Audit log
        auditService.logAction(
                moderatorUsername,
                AuditAction.HOMEBREW_MOVED_TO_DRAFT,
                "HOMEBREW",
                homebrewId,
                "Sebep: " + action.getReason()
        );

        // Kullanıcıya bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Homebrew içeriğin '" + homebrew.getName() + "' yayından kaldırıldı. Sebep: " + action.getReason();

        notificationService.createNotification(
                homebrew.getAuthor(),
                "⚠️ Homebrew Yayından Kaldırıldı",
                notificationMessage,
                NotificationType.SYSTEM,
                "/homebrews/me"
        );

        log.info("Homebrew moved to draft - ID: {}, Moderator: {}, Reason: {}",
                homebrewId, moderatorUsername, action.getReason());
    }

    /**
     * Homebrew metadata düzenle
     * Moderatör sadece title, description, category gibi metadata düzenleyebilir
     */
    @Transactional
    public void updateMetadata(String moderatorUsername, Long homebrewId,
                               String title, String description, String category,
                               ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        HomebrewEntry homebrew = homebrewRepository.findById(homebrewId)
                .orElseThrow(() -> new ApiException("Homebrew bulunamadı", HttpStatus.NOT_FOUND));

        // Metadata güncelle
        if (title != null && !title.isBlank()) {
            homebrew.setName(title);
        }
        if (description != null) {
            homebrew.setDescription(description);
        }
        if (category != null) {
            homebrew.setCategory(HomebrewCategory.valueOf(category));
        }

        homebrewRepository.save(homebrew);

        // Audit log
        String details = String.format("Metadata güncellendi - Sebep: %s, Değişiklikler: title=%s, description=%s, category=%s",
                action.getReason(), title != null, description != null, category != null);

        auditService.logAction(
                moderatorUsername,
                AuditAction.HOMEBREW_EDITED_BY_MOD,
                "HOMEBREW",
                homebrewId,
                details
        );

        // Kullanıcıya bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Homebrew içeriğinde moderatör tarafından düzenleme yapıldı. Sebep: " + action.getReason();

        notificationService.createNotification(
                homebrew.getAuthor(),
                "✏️ Homebrew Düzenlendi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/homebrew/" + homebrew.getSlug()
        );

        log.info("Homebrew metadata updated - ID: {}, Moderator: {}", homebrewId, moderatorUsername);
    }
}