package com.zarvekule.blog.service;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogCategory;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.exceptions.ApiException;
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

import java.util.HashSet;
import java.util.Set;

/**
 * Blog Moderasyon Service
 *
 * Moderatör yetkileri:
 * - Blog'u drafta alma
 * - Metadata düzenleme (kategori, slug, etiketler, featuredImage)
 * - Yorum silme (CommentModerationService'de)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BlogModerationService {

    private final BlogEntryRepository blogRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    /**
     * Blog'u drafta al (PUBLISHED/ARCHIVED → DRAFT)
     */
    @Transactional
    public void moveToDraft(String moderatorUsername, Long blogId, ModerationAction action) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        BlogEntry blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ApiException("Blog bulunamadı", HttpStatus.NOT_FOUND));

        if (blog.getStatus() == BlogStatus.DRAFT) {
            throw new ApiException("Blog zaten taslak durumunda", HttpStatus.BAD_REQUEST);
        }

        BlogStatus previousStatus = blog.getStatus();

        // Status güncelle
        blog.setStatus(BlogStatus.DRAFT);
        blogRepository.save(blog);

        // Audit log
        String details = String.format("Sebep: %s, Önceki durum: %s",
                action.getReason(), previousStatus);

        auditService.logAction(
                moderatorUsername,
                AuditAction.BLOG_MOVED_TO_DRAFT,
                "BLOG",
                blogId,
                details
        );

        // Kullanıcıya bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Blog yazın '" + blog.getTitle() + "' yayından kaldırıldı. Sebep: " + action.getReason();

        notificationService.createNotification(
                blog.getAuthor(),
                "⚠️ Blog Yayından Kaldırıldı",
                notificationMessage,
                NotificationType.SYSTEM,
                "/blog/bloglarim"
        );

        log.info("Blog moved to draft - ID: {}, Moderator: {}, Reason: {}",
                blogId, moderatorUsername, action.getReason());
    }

    /**
     * Blog metadata düzenle
     * Moderatör: kategori, slug, etiketler, featuredImage düzenleyebilir
     * İçeriği (content) düzenleyemez
     */
    @Transactional
    public void updateMetadata(
            String moderatorUsername,
            Long blogId,
            String category,
            String slug,
            String tags,
            String featuredImage,
            ModerationAction action
    ) {
        User moderator = userRepository.findByUsername(moderatorUsername)
                .orElseThrow(() -> new ApiException("Moderatör bulunamadı", HttpStatus.UNAUTHORIZED));

        BlogEntry blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ApiException("Blog bulunamadı", HttpStatus.NOT_FOUND));

        StringBuilder changes = new StringBuilder("Değişiklikler: ");

        // Metadata güncelle
        if (category != null && !category.isBlank()) {
            blog.setCategory(BlogCategory.valueOf(category));
            changes.append("category=").append(category).append(", ");
        }

        if (slug != null && !slug.isBlank()) {
            // Slug uniqueness kontrolü
            if (blogRepository.existsBySlugAndIdNot(slug, blogId)) {
                throw new ApiException("Bu slug zaten kullanımda", HttpStatus.CONFLICT);
            }
            blog.setSlug(slug);
            changes.append("slug=").append(slug).append(", ");
        }

        if (tags != null) {
            Set<String> tagList = new HashSet<>();
            tagList.add(tags);
            blog.setTags(tagList);
            changes.append("tags=").append(tags).append(", ");
        }

        if (featuredImage != null) {
            blog.setThumbnailUrl(featuredImage);
            changes.append("featuredImage=").append(featuredImage);
        }

        blogRepository.save(blog);

        // Audit log
        String details = String.format("Sebep: %s, %s", action.getReason(), changes);

        auditService.logAction(
                moderatorUsername,
                AuditAction.BLOG_METADATA_UPDATED,
                "BLOG",
                blogId,
                details
        );

        // Kullanıcıya bildirim
        String notificationMessage = action.getMessageToUser() != null
                ? action.getMessageToUser()
                : "Blog yazınızda moderatör tarafından düzenleme yapıldı. Sebep: " + action.getReason();

        notificationService.createNotification(
                blog.getAuthor(),
                "✏️ Blog Düzenlendi",
                notificationMessage,
                NotificationType.SYSTEM,
                "/blog/" + blog.getSlug()
        );

        log.info("Blog metadata updated - ID: {}, Moderator: {}, Changes: {}",
                blogId, moderatorUsername, changes);
    }
}