package com.zarvekule.gamification.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.entity.Badge;
import com.zarvekule.gamification.entity.UserBadge;
import com.zarvekule.gamification.entity.UserStats;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.enums.RankTier;
import com.zarvekule.gamification.repository.BadgeRepository;
import com.zarvekule.gamification.repository.UserBadgeRepository;
import com.zarvekule.gamification.repository.UserStatsRepository;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationServiceImpl implements GamificationService {

    private final UserRepository userRepository;
    private final UserStatsRepository statsRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public void initStats(User user) {
        if (statsRepository.findByUser_Id(user.getId()).isEmpty()) {
            UserStats stats = new UserStats();
            stats.setUser(user);
            statsRepository.save(stats);
        }
    }

    @Override
    @Transactional
    public void assignBadgeToUser(String adminUsername, Long userId, String badgeCode) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        checkAndGiveBadge(targetUser, badgeCode, true);
    }

    @Override
    @Transactional
    public void processAction(User user, ActionType action) {
        UserStats stats = statsRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserStats s = new UserStats();
                    s.setUser(user);
                    return statsRepository.save(s);
                });

        stats.setCurrentXp(stats.getCurrentXp() + action.getXpValue());

        RankTier newRank = RankTier.getRankByXp(stats.getCurrentXp());
        if (newRank != stats.getCurrentRank()) {
            stats.setCurrentRank(newRank);
            notificationService.createNotification(
                    user,
                    "Rütbe Atladın! 🆙",
                    "Yeni rütben: " + newRank.getTitle(),
                    NotificationType.SYSTEM,
                    "/profile"
            );
        }

        updateCountsAndCheckBadges(user, stats, action);

        statsRepository.save(stats);
    }

    private void updateCountsAndCheckBadges(User user, UserStats stats, ActionType action) {
        switch (action) {
            case POST_COMMENT -> {
                stats.setTotalComments(stats.getTotalComments() + 1);
                checkAndGiveBadge(user, "FIRST_COMMENT", stats.getTotalComments() >= 1);
                checkAndGiveBadge(user, "COMMENT_10", stats.getTotalComments() >= 10);
                checkAndGiveBadge(user, "COMMENT_50", stats.getTotalComments() >= 50);
                checkAndGiveBadge(user, "COMMENT_200", stats.getTotalComments() >= 200);
                checkAndGiveBadge(user, "COMMENT_500", stats.getTotalComments() >= 500);
            }
            case CREATE_HOMEBREW -> {
                stats.setTotalHomebrews(stats.getTotalHomebrews() + 1);
                checkAndGiveBadge(user, "FIRST_HOMEBREW", stats.getTotalHomebrews() >= 1);
                checkAndGiveBadge(user, "BREWER_1", stats.getTotalHomebrews() >= 1);
                checkAndGiveBadge(user, "BREWER_10", stats.getTotalHomebrews() >= 10);
                checkAndGiveBadge(user, "BREWER_50", stats.getTotalHomebrews() >= 50);
                checkAndGiveBadge(user, "BREWER_100", stats.getTotalHomebrews() >= 100);
            }
            case CREATE_BLOG -> {
                stats.setTotalBlogs(stats.getTotalBlogs() + 1);
                checkAndGiveBadge(user, "FIRST_BLOG", stats.getTotalBlogs() >= 1);
            }
            case RECEIVE_LIKE -> {
                stats.setTotalLikesReceived(stats.getTotalLikesReceived() + 1);
                checkAndGiveBadge(user, "MID_LIKED", stats.getTotalLikesReceived() >= 100);
                checkAndGiveBadge(user, "MOST_LIKED", stats.getTotalLikesReceived() >= 200);
            }
        }
    }

    private void checkAndGiveBadge(User user, String conditionCode, boolean conditionMet) {
        if (!conditionMet) return;

        if (userBadgeRepository.existsByUser_IdAndBadge_ConditionCode(user.getId(), conditionCode)) {
            return;
        }

        Badge badge = badgeRepository.findByConditionCode(conditionCode).orElse(null);

        if (badge != null) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadgeRepository.save(userBadge);

            // 5. Bildirim Gönder
            notificationService.createNotification(
                    user,
                    "Yeni Rozet Kazandın! 🏆",
                    "Koleksiyonuna '" + badge.getName() + "' eklendi.",
                    NotificationType.SYSTEM,
                    "/profile"
            );

            log.info("Kullanıcı {} yeni rozet kazandı: {}", user.getUsername(), badge.getName());
        }
    }

    @Scheduled(cron = "0 0 4 * * *")
    @Transactional
    public void checkMembershipBadges() {
        // Not: Kullanıcı sayısı milyonlara ulaşırsa bu sorgu optimize edilmeli (Pagination ile).
        List<User> allUsers = userRepository.findAll();

        LocalDateTime now = LocalDateTime.now();

        for (User user : allUsers) {
            long months = ChronoUnit.MONTHS.between(user.getCreatedAt(), now);

            if (months >= 3) {
                checkAndGiveBadge(user, "MEMBER_3M", true);
            }
            if (months >= 12) {
                checkAndGiveBadge(user, "MEMBER_1Y", true);
            }
            if (months >= 24) {
                checkAndGiveBadge(user, "MEMBER_2Y", true);
            }
        }
    }
}