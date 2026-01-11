package com.zarvekule.moderation.service;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.audit.repository.AuditLogRepository;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.moderation.dto.ModerationStatsDTO;
import com.zarvekule.venue.enums.VenueStatus;
import com.zarvekule.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Moderation Stats Service
 * Dashboard istatistiklerini hesaplar
 */
@Service
@RequiredArgsConstructor
public class ModerationStatsService {

    private final HomebrewEntryRepository homebrewRepository;
    private final VenueRepository venueRepository;
    private final AuditLogRepository auditLogRepository;

    /**
     * Moderasyon istatistiklerini getir
     */
    @Transactional(readOnly = true)
    public ModerationStatsDTO getStats() {
        ModerationStatsDTO stats = new ModerationStatsDTO();

        // 1. Bekleyen işlemler (PENDING homebrew + PENDING venue)
        long pendingHomebrews = homebrewRepository.countByStatus(HomebrewStatus.PENDING_APPROVAL);
        long pendingVenues = venueRepository.countByStatus(VenueStatus.PENDING_APPROVAL);
        stats.setPendingItems(pendingHomebrews + pendingVenues);

        // 2. Bugün başlangıç ve bitiş zamanı
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // 3. Bugün onaylanan (APPROVED actions)
        List<AuditLog> todayLogs = auditLogRepository.findAllByCreatedAtBetween(startOfDay, endOfDay);

        long approvedToday = todayLogs.stream()
                .filter(log -> log.getAction().name().contains("APPROVED"))
                .count();
        stats.setApprovedToday(approvedToday);

        // 4. Bugün reddedilen (REJECTED actions)
        long rejectedToday = todayLogs.stream()
                .filter(log -> log.getAction().name().contains("REJECTED"))
                .count();
        stats.setRejectedToday(rejectedToday);

        // 5. Toplam işlem sayısı
        long totalActions = auditLogRepository.count();
        stats.setTotalActions(totalActions);

        return stats;
    }
}