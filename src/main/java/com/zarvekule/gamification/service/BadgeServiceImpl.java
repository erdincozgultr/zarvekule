package com.zarvekule.gamification.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.dto.BadgeDto;
import com.zarvekule.gamification.entity.Badge;
import com.zarvekule.gamification.entity.UserBadge;
import com.zarvekule.gamification.repository.BadgeRepository;
import com.zarvekule.gamification.repository.UserBadgeRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<BadgeDto> getAllBadges() {
        List<Badge> badges = badgeRepository.findAll();

        return badges.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BadgeDto> getUserBadges(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        // Kullanıcının kazandığı badge'leri al
        List<UserBadge> userBadges = userBadgeRepository.findAllByUser_Id(user.getId());

        // Badge ID -> UserBadge map oluştur
        Map<Long, UserBadge> userBadgeMap = userBadges.stream()
                .collect(Collectors.toMap(
                        ub -> ub.getBadge().getId(),
                        ub -> ub
                ));

        // Tüm badge'leri al ve kullanıcının kazandıklarını işaretle
        List<Badge> allBadges = badgeRepository.findAll();

        return allBadges.stream()
                .map(badge -> toDto(badge, userBadgeMap.get(badge.getId())))
                .collect(Collectors.toList());
    }

    // ==================== HELPER METHODS ====================

    /**
     * Badge'i DTO'ya çevir (kullanıcı bilgisi olmadan)
     */
    private BadgeDto toDto(Badge badge) {
        BadgeDto dto = new BadgeDto();
        dto.setId(badge.getId());
        dto.setName(badge.getName());
        dto.setDescription(badge.getDescription());
        dto.setIconUrl(badge.getIconUrl());
        dto.setConditionCode(badge.getConditionCode());
        dto.setEarned(false);
        dto.setEarnedAt(null);
        return dto;
    }

    // BadgeServiceImpl.java - toDto metodunda
    private BadgeDto toDto(Badge badge, UserBadge userBadge) {
        BadgeDto dto = new BadgeDto();
        dto.setId(badge.getId());
        dto.setName(badge.getName());
        dto.setDescription(badge.getDescription());
        dto.setIconUrl(badge.getIconUrl());
        dto.setConditionCode(badge.getConditionCode());
        dto.setCategory(badge.getCategory().name());

        if (userBadge != null) {
            dto.setEarned(true);
            dto.setEarnedAt(userBadge.getEarnedAt());
        } else {
            dto.setEarned(false);
        }

        return dto;
    }
}