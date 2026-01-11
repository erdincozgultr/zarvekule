package com.zarvekule.moderation.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.blog.entity.BlogComment;
import com.zarvekule.blog.repository.BlogCommentRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.homebrew.entity.HomebrewComment;
import com.zarvekule.homebrew.repository.HomebrewCommentRepository;
import com.zarvekule.moderation.dto.ModerationAction;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.wiki.entity.WikiComment;
import com.zarvekule.wiki.repository.WikiCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Comment Moderation Service
 *
 * Tüm yorum tipleri için ortak moderasyon servisi
 * - Homebrew yorumları
 * - Wiki yorumları
 * - Blog yorumları
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentModerationService {

    private final HomebrewCommentRepository homebrewCommentRepository;
    private final WikiCommentRepository wikiCommentRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /**
     * Homebrew yorumu sil
     */
    @Transactional
    public void deleteHomebrewComment(String moderatorUsername, Long commentId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        HomebrewComment comment = homebrewCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı", HttpStatus.NOT_FOUND));

        User commentAuthor = comment.getUser();
        Long homebrewId = comment.getHomebrew().getId();

        // Yorum sil
        homebrewCommentRepository.delete(comment);

        // Audit log
        String details = String.format("Sebep: %s, Yorum sahibi: %s, Homebrew ID: %d",
                action.getReason(), commentAuthor.getUsername(), homebrewId);

        auditService.logAction(
                moderatorUsername,
                AuditAction.HOMEBREW_COMMENT_DELETED,
                "HOMEBREW_COMMENT",
                commentId,
                details
        );

        // Yorum sahibine bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Homebrew'da yazdığınız bir yorum silindi. Sebep: " + action.getReason();

        notificationService.createNotification(
                commentAuthor,
                "🗑️ Yorumunuz Silindi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/homebrew/" + comment.getHomebrew().getSlug()
        );

        log.info("Homebrew comment deleted - Comment ID: {}, Moderator: {}, Reason: {}",
                commentId, moderatorUsername, action.getReason());
    }

    /**
     * Wiki yorumu sil
     */
    @Transactional
    public void deleteWikiComment(String moderatorUsername, Long commentId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        WikiComment comment = wikiCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı", HttpStatus.NOT_FOUND));

        User commentAuthor = comment.getUser();
        Long wikiId = comment.getWiki().getId();

        // Yorum sil
        wikiCommentRepository.delete(comment);

        // Audit log
        String details = String.format("Sebep: %s, Yorum sahibi: %s, Wiki ID: %d",
                action.getReason(), commentAuthor.getUsername(), wikiId);

        auditService.logAction(
                moderatorUsername,
                AuditAction.WIKI_COMMENT_DELETED,
                "WIKI_COMMENT",
                commentId,
                details
        );

        // Yorum sahibine bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Wiki sayfasında yazdığınız bir yorum silindi. Sebep: " + action.getReason();

        notificationService.createNotification(
                commentAuthor,
                "🗑️ Yorumunuz Silindi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/wiki/" + comment.getWiki().getSlug()
        );

        log.info("Wiki comment deleted - Comment ID: {}, Moderator: {}, Reason: {}",
                commentId, moderatorUsername, action.getReason());
    }

    /**
     * Blog yorumu sil
     */
    @Transactional
    public void deleteBlogComment(String moderatorUsername, Long commentId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        BlogComment comment = blogCommentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı", HttpStatus.NOT_FOUND));

        User commentAuthor = comment.getUser();
        Long blogId = comment.getBlog().getId();

        // Yorum sil
        blogCommentRepository.delete(comment);

        // Audit log
        String details = String.format("Sebep: %s, Yorum sahibi: %s, Blog ID: %d",
                action.getReason(), commentAuthor.getUsername(), blogId);

        auditService.logAction(
                moderatorUsername,
                AuditAction.BLOG_COMMENT_DELETED,
                "BLOG_COMMENT",
                commentId,
                details
        );

        // Yorum sahibine bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Blog yazısında yazdığınız bir yorum silindi. Sebep: " + action.getReason();

        notificationService.createNotification(
                commentAuthor,
                "🗑️ Yorumunuz Silindi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/blog/" + comment.getBlog().getSlug()
        );

        log.info("Blog comment deleted - Comment ID: {}, Moderator: {}, Reason: {}",
                commentId, moderatorUsername, action.getReason());
    }
}