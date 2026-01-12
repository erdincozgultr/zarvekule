package com.zarvekule.moderation.service;

import com.zarvekule.audit.entity.AuditLog;
import com.zarvekule.audit.repository.AuditLogRepository;
import com.zarvekule.blog.dto.BlogEntrySummary;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.blog.mapper.BlogEntryMapper;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.campaign.dto.CampaignResponse;
import com.zarvekule.campaign.entity.Campaign;
import com.zarvekule.campaign.mapper.CampaignMapper;
import com.zarvekule.campaign.repository.CampaignRepository;
import com.zarvekule.gamification.dto.GuildDto;
import com.zarvekule.gamification.entity.Guild;
import com.zarvekule.gamification.mapper.GuildMapper;
import com.zarvekule.gamification.repository.GuildRepository;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.mapper.HomebrewEntryMapper;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.venue.dto.VenueResponse;
import com.zarvekule.venue.entity.Venue;
import com.zarvekule.venue.enums.VenueStatus;
import com.zarvekule.venue.mapper.VenueMapper;
import com.zarvekule.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationDashboardService {

    // Repositories
    private final HomebrewEntryRepository homebrewRepository;
    private final BlogEntryRepository blogRepository;
    private final GuildRepository guildRepository;
    private final CampaignRepository campaignRepository;
    private final VenueRepository venueRepository;
    private final AuditLogRepository auditLogRepository;

    // Mappers
    private final HomebrewEntryMapper homebrewMapper;
    private final BlogEntryMapper blogMapper;
    private final GuildMapper guildMapper;
    private final CampaignMapper campaignMapper;
    private final VenueMapper venueMapper;

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
     * Yayınlanmış blogları getir
     */
    @Transactional(readOnly = true)
    public Page<BlogEntrySummary> getPublishedBlogs(Pageable pageable) {
        Page<BlogEntry> published = blogRepository.findAllByStatus(BlogStatus.PUBLISHED, pageable);
        return published.map(blogMapper::toSummaryDto);
    }

    // ============================================
    // GUILD
    // ============================================

    /**
     * Tüm loncaları getir
     */
    @Transactional(readOnly = true)
    public Page<GuildDto> getAllGuilds(Pageable pageable) {
        return guildRepository.findAll(pageable)
                .map(guildMapper::toDto);
    }


    // ============================================
    // CAMPAIGN
    // ============================================

    /**
     * Tüm kampanyaları getir
     */
    @Transactional(readOnly = true)
    public Page<CampaignResponse> getAllCampaigns(Pageable pageable) {
        return campaignRepository.findAll(pageable)
                .map(campaignMapper::toResponse);
    }

    // ============================================
    // VENUE
    // ============================================

    /**
     * Onay bekleyen venue'leri getir
     */
    @Transactional(readOnly = true)
    public Page<VenueResponse> getPendingVenues(Pageable pageable) {
        return venueRepository.findAllByStatus(VenueStatus.PENDING_APPROVAL, pageable)
                .map(venueMapper::toResponse);
    }

    /**
     * Tüm venue'leri getir (status filter ile)
     */
    @Transactional(readOnly = true)
    public Page<VenueResponse> getAllVenues(VenueStatus status, Pageable pageable) {
        Page<Venue> venues;
        if (status != null) {
            venues = venueRepository.findAllByStatus(status, pageable);
        } else {
            venues = venueRepository.findAll(pageable);
        }
        return venues.map(venueMapper::toResponse);
    }

    // ============================================
    // AUDIT LOG
    // ============================================

    /**
     * Belirli target için audit log'ları getir
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

    /**
     * Yasaklı loncaları getir
     */
    @Transactional(readOnly = true)
    public Page<GuildDto> getBannedGuilds(Pageable pageable) {
        return guildRepository.findAllByIsBannedTrue(pageable)
                .map(guildMapper::toDto);
    }


}