package com.zarvekule.user.dto;

import com.zarvekule.user.entity.PrivacySettings;

public record PrivacySettingsDto(
        String profileVisibility,
        boolean showActivity,
        boolean showBadges,
        boolean showGuild,
        boolean showStats
) {
    public static PrivacySettingsDto fromEntity(PrivacySettings entity) {
        if (entity == null) {
            // Default değerler
            return new PrivacySettingsDto("PUBLIC", true, true, true, true);
        }
        return new PrivacySettingsDto(
                entity.getProfileVisibility().name(),
                entity.isShowActivity(),
                entity.isShowBadges(),
                entity.isShowGuild(),
                entity.isShowStats()
        );
    }
}