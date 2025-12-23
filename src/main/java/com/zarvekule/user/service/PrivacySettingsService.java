package com.zarvekule.user.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.user.dto.PrivacySettingsDto;
import com.zarvekule.user.entity.PrivacySettings;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.PrivacySettingsRepository;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PrivacySettingsService {

    private final PrivacySettingsRepository privacySettingsRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PrivacySettingsDto getPrivacySettings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        PrivacySettings settings = privacySettingsRepository.findByUser_Id(user.getId()).orElse(null);
        return PrivacySettingsDto.fromEntity(settings);
    }

    @Transactional
    public PrivacySettingsDto updatePrivacySettings(String username, PrivacySettingsDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        PrivacySettings settings = privacySettingsRepository.findByUser_Id(user.getId())
                .orElse(new PrivacySettings());

        if (settings.getId() == null) {
            settings.setUser(user);
        }

        // Update settings
        try {
            settings.setProfileVisibility(
                    PrivacySettings.ProfileVisibility.valueOf(dto.profileVisibility())
            );
        } catch (IllegalArgumentException e) {
            throw new ApiException("Geçersiz görünürlük değeri", HttpStatus.BAD_REQUEST);
        }

        settings.setShowActivity(dto.showActivity());
        settings.setShowBadges(dto.showBadges());
        settings.setShowGuild(dto.showGuild());
        settings.setShowStats(dto.showStats());

        PrivacySettings saved = privacySettingsRepository.save(settings);
        return PrivacySettingsDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public boolean canViewProfile(String viewerUsername, String targetUsername) {
        if (viewerUsername != null && viewerUsername.equals(targetUsername)) {
            return true; // Kendi profilini her zaman görebilir
        }

        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        PrivacySettings settings = privacySettingsRepository.findByUser_Id(targetUser.getId())
                .orElse(null);

        if (settings == null) {
            return true; // Default: public
        }

        return settings.getProfileVisibility() == PrivacySettings.ProfileVisibility.PUBLIC;
    }
}