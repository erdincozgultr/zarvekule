package com.zarvekule.gamification.service;

import com.zarvekule.gamification.dto.BadgeDto;

import java.util.List;

public interface BadgeService {

    /**
     * Tüm badge'leri listele (public, login gerekmez)
     */
    List<BadgeDto> getAllBadges();

    /**
     * Kullanıcının badge'lerini listele (login gerekli)
     * @param username Kullanıcı adı
     * @return Tüm badge'ler, kullanıcının kazandıkları işaretli
     */
    List<BadgeDto> getUserBadges(String username);
}