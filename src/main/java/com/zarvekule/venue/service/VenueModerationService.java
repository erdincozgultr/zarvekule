package com.zarvekule.venue.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.moderation.dto.ModerationAction;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.venue.entity.Venue;
import com.zarvekule.venue.enums.VenueStatus;
import com.zarvekule.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Venue (Dost Mekanlar) Moderasyon Service
 *
 * Moderatör yetkileri:
 * - Venue onaylama (PENDING_APPROVAL → PUBLISHED)
 * - Venue reddetme (PENDING_APPROVAL → REJECTED)
 * - Venue'yü drafta alma (PUBLISHED → PENDING_APPROVAL)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VenueModerationService {

    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /**
     * Venue'yü onayla (PENDING_APPROVAL → PUBLISHED)
     */
    @Transactional
    public void approveVenue(String moderatorUsername, Long venueId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı", HttpStatus.NOT_FOUND));

        if (venue.getStatus() != VenueStatus.PENDING_APPROVAL) {
            throw new ApiException("Sadece onay bekleyen mekanlar onaylanabilir", HttpStatus.BAD_REQUEST);
        }

        // Status güncelle
        venue.setStatus(VenueStatus.PUBLISHED);
        venueRepository.save(venue);

        // Audit log
        String details = String.format("Sebep: %s, Mekan: %s, Şehir: %s",
                action.getReason(), venue.getName(), venue.getCity());

        auditService.logAction(
                moderatorUsername,
                AuditAction.VENUE_APPROVED,
                "VENUE",
                venueId,
                details
        );

        // Oluşturan kişiye bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Eklediğiniz mekan '" + venue.getName() + "' onaylandı ve yayınlandı!";

        notificationService.createNotification(
                venue.getCreatedBy(),
                "✅ Mekan Onaylandı",
                notificationMessage,
                NotificationType.SYSTEM,
                "/mekanlar/" + venue.getId()
        );

        log.info("Venue approved - ID: {}, Name: {}, Moderator: {}, Reason: {}",
                venueId, venue.getName(), moderatorUsername, action.getReason());
    }

    /**
     * Venue'yü reddet (PENDING_APPROVAL → REJECTED)
     */
    @Transactional
    public void rejectVenue(String moderatorUsername, Long venueId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı", HttpStatus.NOT_FOUND));

        if (venue.getStatus() != VenueStatus.PENDING_APPROVAL) {
            throw new ApiException("Sadece onay bekleyen mekanlar reddedilebilir", HttpStatus.BAD_REQUEST);
        }

        // Status güncelle
        venue.setStatus(VenueStatus.REJECTED);
        venueRepository.save(venue);

        // Audit log
        String details = String.format("Sebep: %s, Mekan: %s, Şehir: %s",
                action.getReason(), venue.getName(), venue.getCity());

        auditService.logAction(
                moderatorUsername,
                AuditAction.VENUE_REJECTED,
                "VENUE",
                venueId,
                details
        );

        // Oluşturan kişiye bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Eklediğiniz mekan '" + venue.getName() + "' reddedildi. Sebep: " + action.getReason();

        notificationService.createNotification(
                venue.getCreatedBy(),
                "❌ Mekan Reddedildi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/mekanlar"
        );

        log.info("Venue rejected - ID: {}, Name: {}, Moderator: {}, Reason: {}",
                venueId, venue.getName(), moderatorUsername, action.getReason());
    }

    /**
     * Venue'yü drafta al (PUBLISHED → PENDING_APPROVAL)
     */
    @Transactional
    public void moveToDraft(String moderatorUsername, Long venueId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ApiException("Mekan bulunamadı", HttpStatus.NOT_FOUND));

        if (venue.getStatus() != VenueStatus.PUBLISHED) {
            throw new ApiException("Sadece yayında olan mekanlar drafta alınabilir", HttpStatus.BAD_REQUEST);
        }

        // Status güncelle
        venue.setStatus(VenueStatus.PENDING_APPROVAL);
        venueRepository.save(venue);

        // Audit log
        String details = String.format("Sebep: %s, Mekan: %s, Şehir: %s",
                action.getReason(), venue.getName(), venue.getCity());

        auditService.logAction(
                moderatorUsername,
                AuditAction.VENUE_MOVED_TO_DRAFT,
                "VENUE",
                venueId,
                details
        );

        // Oluşturan kişiye bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Mekan '" + venue.getName() + "' yayından kaldırıldı. Sebep: " + action.getReason();

        notificationService.createNotification(
                venue.getCreatedBy(),
                "⚠️ Mekan Yayından Kaldırıldı",
                notificationMessage,
                NotificationType.SYSTEM,
                "/mekanlar"
        );

        log.info("Venue moved to draft - ID: {}, Name: {}, Moderator: {}, Reason: {}",
                venueId, venue.getName(), moderatorUsername, action.getReason());
    }
}