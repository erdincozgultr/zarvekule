package com.zarvekule.moderation.service;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.audit.repository.AuditLogRepository;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.blog.mapper.BlogEntryMapper;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.mapper.HomebrewEntryMapper;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Moderation Dashboard Service
 * Moderatörlerin içerikleri görüntülemesi için endpoint'ler
 */
@Service
@RequiredArgsConstructor
public class ModerationDashboardService {

    private final HomebrewEntryRepository homebrewRepository;
    private final HomebrewEntryMapper homebrewMapper;
    private final BlogEntryRepository blogRepository;
    private final BlogEntryMapper blogMapper;
    private final AuditLogRepository auditLogRepository;

    // ============================================
    // HOMEBREW
    // ============================================

    /**
     * Onay bekleyen homebrew'ları getir
     */
    @Transactional(readOnly = true)
    public Page<HomebrewEntryResponse> getPendingHomebrews(Pageable pageable) {
        Page<HomebrewEntry> pending = homebrewRepository.findAllByStatus(HomebrewStatus.PENDING_APPROVAL, pageable);
        return pending.map(homebrewMapper::toResponseDto);
    }

    /**
     * Tüm homebrew'ları getir (status filter ile)
     */
    @Transactional(readOnly = true)
    public Page<HomebrewEntryResponse> getAllHomebrews(HomebrewStatus status, Pageable pageable) {
        Page<HomebrewEntry> entries;
        if (status != null) {
            entries = homebrewRepository.findAllByStatus(status, pageable);
        } else {
            entries = homebrewRepository.findAll(pageable);
        }
        return entries.map(homebrewMapper::toResponseDto);
    }

    // ============================================
    // BLOG
    // ============================================

    /**
     * Tüm blogları getir (status filter ile)
     */
    @Transactional(readOnly = true)
    public Page<BlogEntrySummary> getAllBlogs(BlogStatus status, Pageable pageable) {
        Page<BlogEntry> entries;
        if (status != null) {
            entries = blogRepository.findAllByStatus(status, pageable);
        } else {
            entries = blogRepository.findAll(pageable);
        }
        return entries.map(blogMapper::toSummaryDto);
    }

    /**
     * Yayında olan blogları getir
     */
    @Transactional(readOnly = true)
    public Page<BlogEntrySummary> getPublishedBlogs(Pageable pageable) {
        Page<BlogEntry> published = blogRepository.findAllByStatus(BlogStatus.PUBLISHED, pageable);
        return published.map(blogMapper::toSummaryDto);
    }

    // ============================================
    // AUDIT
    // ============================================

    /**
     * Belirli bir içerik için audit log'ları getir
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogs(String targetType, Long targetId) {
        return auditLogRepository.findAllByTargetTypeAndTargetIdOrderByCreatedAtDesc(targetType, targetId);
    }

    /**
     * Tüm audit log'ları getir (paginated)
     */
    @Transactional(readOnly = true)
    public Page<AuditLog> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}