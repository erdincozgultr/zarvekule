package com.zarvekule.gamification.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.moderation.dto.ModerationAction;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Guild Moderasyon Service
 *
 * Moderatör yetkileri:
 * - Guild soft ban (görünürlüğü kaldır)
 * - Guild unban (görünürlüğü geri ver)
 *
 * NOT: Guild silinmez, sadece banned duruma alınır
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GuildModerationService {

    private final GuildRepository guildRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /**
     * Guild'i soft ban yap (görünürlüğü kaldır)
     */
    @Transactional
    public void banGuild(String moderatorUsername, Long guildId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        if (guild.isBanned()) {
            throw new ApiException("Lonca zaten yasaklanmış durumda", HttpStatus.BAD_REQUEST);
        }

        // Soft ban uygula
        guild.setBanned(true);
        guild.setBanReason(action.getReason());
        guild.setBannedAt(LocalDateTime.now());
        guild.setBannedById(moderator.getId());

        guildRepository.save(guild);

        // Audit log
        String details = String.format("Sebep: %s, Lonca: %s, Üye sayısı: %d",
                action.getReason(), guild.getName(), guild.getMembers().size());

        auditService.logAction(
                moderatorUsername,
                AuditAction.GUILD_SOFT_BANNED,
                "GUILD",
                guildId,
                details
        );

        // Lonca liderine bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Loncanız '" + guild.getName() + "' görünürlükten kaldırıldı. Sebep: " + action.getReason();

        if (guild.getLeader() != null) {
            notificationService.createNotification(
                    guild.getLeader(),
                    "⚠️ Lonca Yasaklandı",
                    notificationMessage,
                    NotificationType.SYSTEM,
                    "/guilds"
            );
        }

        // Tüm üyelere de bildirim gönderilebilir (opsiyonel)
        guild.getMembers().forEach(member -> {
            if (!member.getId().equals(guild.getLeader().getId())) {
                notificationService.createNotification(
                        member,
                        "⚠️ Loncanız Yasaklandı",
                        "Üyesi olduğunuz '" + guild.getName() + "' loncası görünürlükten kaldırıldı.",
                        NotificationType.SYSTEM,
                        "/guilds"
                );
            }
        });

        log.info("Guild banned - ID: {}, Name: {}, Moderator: {}, Reason: {}",
                guildId, guild.getName(), moderatorUsername, action.getReason());
    }

    /**
     * Guild'in yasağını kaldır
     */
    @Transactional
    public void unbanGuild(String moderatorUsername, Long guildId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        Guild guild = guildRepository.findById(guildId)
                .orElseThrow(() -> new ApiException("Lonca bulunamadı", HttpStatus.NOT_FOUND));

        if (!guild.isBanned()) {
            throw new ApiException("Lonca zaten yasaklı değil", HttpStatus.BAD_REQUEST);
        }

        // Yasağı kaldır
        guild.setBanned(false);
        guild.setBanReason(null);
        guild.setBannedAt(null);
        guild.setBannedById(null);

        guildRepository.save(guild);

        // Audit log
        String details = String.format("Sebep: %s, Lonca: %s",
                action.getReason(), guild.getName());

        auditService.logAction(
                moderatorUsername,
                AuditAction.GUILD_UNBANNED,
                "GUILD",
                guildId,
                details
        );

        // Lonca liderine bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Loncanız '" + guild.getName() + "' tekrar aktif duruma getirildi!";

        if (guild.getLeader() != null) {
            notificationService.createNotification(
                    guild.getLeader(),
                    "✅ Lonca Yasağı Kaldırıldı",
                    notificationMessage,
                    NotificationType.SYSTEM,
                    "/guilds/" + guild.getId()
            );
        }

        log.info("Guild unbanned - ID: {}, Name: {}, Moderator: {}",
                guildId, guild.getName(), moderatorUsername);
    }
}