package com.zarvekule.moderation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Moderation Stats DTO
 * Dashboard istatistikleri için
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationStatsDTO {

    /**
     * Bekleyen işlemler (PENDING homebrew + PENDING venue)
     */
    private long pendingItems;

    /**
     * Bugün onaylanan (APPROVED actions today)
     */
    private long approvedToday;

    /**
     * Bugün reddedilen (REJECTED actions today)
     */
    private long rejectedToday;

    /**
     * Toplam işlem (total audit logs)
     */
    private long totalActions;
}