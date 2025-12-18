package com.zarvekule.gamification.service;

import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.user.entity.User;

public interface GamificationService {
    void processAction(User user, ActionType action);

    void initStats(User user);

    void assignBadgeToUser(String adminUsername, Long userId, String badgeCode);
}