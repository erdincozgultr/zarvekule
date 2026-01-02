package com.zarvekule.gamification.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.entity.Badge;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.entity.UserBadge;
import com.zarvekule.gamification.entity.UserStats;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.enums.RankTier;
import com.zarvekule.gamification.repository.BadgeRepository;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.gamification.repository.UserBadgeRepository;
import com.zarvekule.gamification.repository.UserStatsRepository;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationServiceImpl implements GamificationService {

    private final UserRepository userRepository;
    private final UserStatsRepository statsRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final GuildRepository guildRepository;  // ✅ YENİ
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
        // 1. Get or create user stats
        UserStats stats = statsRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    UserStats s = new UserStats();
                    s.setUser(user);
                    return statsRepository.save(s);
                });

        // 2. Add individual XP
        long oldXp = stats.getCurrentXp();
        stats.setCurrentXp(oldXp + action.getXpValue());

        // 3. Check rank change
        RankTier oldRank = stats.getCurrentRank();
        RankTier newRank = RankTier.getRankByXp(stats.getCurrentXp());

        if (newRank != oldRank) {
            stats.setCurrentRank(newRank);
            notificationService.createNotification(
                    user,
                    "🎉 Rütbe Atladın!",
                    "Yeni rütben: " + newRank.getTitle() + " (+" + action.getXpValue() + " XP)",
                    NotificationType.SYSTEM,
                    "/profil/" + user.getUsername()
            );
            log.info("User {} ranked up! {} -> {} (XP: {})",
                    user.getUsername(), oldRank.getTitle(), newRank.getTitle(), stats.getCurrentXp());
        }

        // 4. Update counts and check badges
        updateCountsAndCheckBadges(user, stats, action);

        // 5. Save user stats
        statsRepository.save(stats);

        // ✅ 6. GUILD XP (YENİ!)
        processGuildXp(user, action);

        log.debug("Action processed - User: {}, Action: {}, XP: {} (+{})",
                user.getUsername(), action.name(), stats.getCurrentXp(), action.getXpValue());
    }

    /**
     * ✅ YENİ: Guild XP sistemi
     */
    private void processGuildXp(User user, ActionType action) {
        // Kullanıcının guild'ini bul
        Guild guild = guildRepository.findByMemberId(user.getId()).orElse(null);

        if (guild == null) {
            log.debug("User {} is not in a guild, skipping guild XP", user.getUsername());
            return;
        }

        // Guild XP hesapla (bireysel XP'nin yarısı)
        int guildXpAmount = action.getXpValue() / 2;

        long oldGuildXp = guild.getXp();
        long newGuildXp = oldGuildXp + guildXpAmount;
        guild.setXp(newGuildXp);

        // Guild level hesapla
        int oldLevel = calculateGuildLevel(oldGuildXp);
        int newLevel = calculateGuildLevel(newGuildXp);
        guild.setLevel(newLevel);

        // Level atladı mı?
        if (newLevel > oldLevel) {
            // Tüm üyelere bildirim
            guild.getMembers().forEach(member -> {
                notificationService.createNotification(
                        member,
                        "🏰 Lonca Seviye Atladı!",
                        guild.getName() + " " + newLevel + ". seviyeye ulaştı! (+" + guildXpAmount + " XP)",
                        NotificationType.GUILD,
                        "/taverna/loncalar/" + guild.getId()
                );
            });

            log.info("Guild {} leveled up! {} -> {} (XP: {})",
                    guild.getName(), oldLevel, newLevel, newGuildXp);
        }

        guildRepository.save(guild);

        log.debug("Guild XP processed - Guild: {}, XP: {} (+{}), Level: {}",
                guild.getName(), newGuildXp, guildXpAmount, newLevel);
    }

    /**
     * Guild level hesaplama
     * Level 1:     0 - 4,999 XP
     * Level 2: 5,000 - 14,999 XP
     * Level 3: 15,000 - 29,999 XP
     * Formula: 5000 * level * level
     */
    private int calculateGuildLevel(long xp) {
        if (xp < 5000) return 1;

        int level = 1;
        long xpNeeded = 0;

        while (xpNeeded <= xp) {
            level++;
            xpNeeded += 5000L * level * level;
        }

        return level - 1;
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

            notificationService.createNotification(
                    user,
                    "🏆 Yeni Rozet Kazandın!",
                    "Koleksiyonuna '" + badge.getName() + "' eklendi.",
                    NotificationType.BADGE_EARNED,
                    "/profil/" + user.getUsername() + "?tab=badges"
            );

            log.info("Badge earned - User: {}, Badge: {}", user.getUsername(), badge.getName());
        }
    }
}