package com.zarvekule.homebrew.service;

import com.zarvekule.homebrew.dto.HomebrewEntryListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.stream.Collectors;

import com.zarvekule.audit.enums.AuditAction;
import com.zarvekule.audit.service.AuditService;
import com.zarvekule.community.enums.TargetType;
import com.zarvekule.community.repository.LikeEntryRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.homebrew.dto.HomebrewEntryPatchRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryRequest;
import com.zarvekule.homebrew.dto.HomebrewEntryResponse;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.mapper.HomebrewEntryMapper;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.notification.enums.NotificationType;
import com.zarvekule.notification.service.NotificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomebrewEntryServiceImpl implements HomebrewEntryService {

    private final HomebrewEntryRepository homebrewRepository;
    private final UserRepository userRepository;
    private final HomebrewEntryMapper homebrewMapper;
    private final NotificationService notificationService;
    private final AuditService auditService;
    private final GamificationService gamificationService;
    private final LikeEntryRepository likeRepository;

    @Override
    @Transactional
    public HomebrewEntryResponse create(String authenticatedUsername, HomebrewEntryRequest request) {
        User author = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Yazar bulunamadı.", HttpStatus.NOT_FOUND));

        String slug = SlugUtils.createSlug(request.getCustomSlug(), request.getName());
        slug = this.ensureUniqueSlug(slug);

        HomebrewEntry entry = new HomebrewEntry();
        entry.setName(request.getName());
        entry.setDescription(request.getDescription());
        entry.setCategory(request.getCategory());
        entry.setContent(request.getContent()); // ✅ FIXED: JSON content eklendi
        entry.setTags(request.getTags());
        entry.setSlug(slug);
        entry.setImageUrl(request.getImageUrl());
        entry.setAuthor(author);
        entry.setStatus(HomebrewStatus.PENDING_APPROVAL);
        entry.setCreatedAt(LocalDateTime.now());

        entry = homebrewRepository.save(entry);
        gamificationService.processAction(entry.getAuthor(), ActionType.CREATE_HOMEBREW);
        return homebrewMapper.toResponseDto(entry);
    }

    @Override
    @Transactional
    public HomebrewEntryResponse update(String authenticatedUsername, Long id, HomebrewEntryPatchRequest request) {
        HomebrewEntry existingEntry = homebrewRepository.findById(id)
                .orElseThrow(() -> new ApiException("Kayıt bulunamadı.", HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        boolean isAdminOrModerator = currentUser.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_MODERATOR"));

        if (!existingEntry.getAuthor().getUsername().equals(authenticatedUsername) && !isAdminOrModerator) {
            throw new ApiException("Bu kaydı güncelleme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        if (request.getName() != null) {
            existingEntry.setName(request.getName());
            String newSlug = SlugUtils.createSlug(request.getCustomSlug(), request.getName());
            existingEntry.setSlug(ensureUniqueSlug(newSlug));
        }
        if (request.getDescription() != null) existingEntry.setDescription(request.getDescription());
        if (request.getCategory() != null) existingEntry.setCategory(request.getCategory());
        if (request.getContent() != null)
            existingEntry.setContent(request.getContent()); // ✅ FIXED: content güncelleme eklendi
        if (request.getTags() != null) existingEntry.setTags(request.getTags());
        if (request.getImageUrl() != null) existingEntry.setImageUrl(request.getImageUrl());

        // Status değişikliği
        if (request.getStatus() != null) {
            HomebrewStatus oldStatus = existingEntry.getStatus();
            existingEntry.setStatus(request.getStatus());

            if (request.getStatus() == HomebrewStatus.PUBLISHED && oldStatus != HomebrewStatus.PUBLISHED) {
                existingEntry.setPublishedAt(LocalDateTime.now());

                // Bildirim gönder
                notificationService.createNotification(
                        existingEntry.getAuthor(),
                        "Homebrew Yayınlandı",
                        "Homebrew içeriğin yayınlandı: " + existingEntry.getName(),
                        NotificationType.HOMEBREW_PUBLISHED,
                        "/homebrew/" + existingEntry.getSlug()
                );

                // Audit log
                auditService.logAction(
                        currentUser.getUsername(),
                        AuditAction.HOMEBREW_APPROVED,
                        "HomebrewEntry",
                        existingEntry.getId(),
                        "Homebrew onaylandı: " + existingEntry.getName()
                );
            }
        }

        existingEntry.setUpdatedAt(LocalDateTime.now());
        return homebrewMapper.toResponseDto(homebrewRepository.save(existingEntry));
    }

    @Override
    @Transactional
    public void delete(String authenticatedUsername, Long id) {
        HomebrewEntry entry = homebrewRepository.findById(id)
                .orElseThrow(() -> new ApiException("Kayıt bulunamadı.", HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED));

        boolean isOwner = Objects.equals(entry.getAuthor().getUsername(), authenticatedUsername);
        boolean isAdminOrModerator = currentUser.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN") || r.getAuthority().equals("ROLE_MODERATOR"));

        if (!isOwner && !isAdminOrModerator) {
            throw new ApiException("Bu kaydı silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        homebrewRepository.delete(entry);

        auditService.logAction(
                currentUser.getUsername(),
                AuditAction.HOMEBREW_REJECTED,
                "HomebrewEntry",
                id,
                "Homebrew silindi: " + entry.getName()
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<HomebrewEntryResponse> getMyHomebrews(String authenticatedUsername) {
        User user = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));
        List<HomebrewEntry> entries = homebrewRepository.findAllByAuthorIdOrderByCreatedAtDesc(user.getId());
        return convertAndCheckLikes(entries, authenticatedUsername);
    }

    @Override
    @Transactional
    public HomebrewEntryResponse getBySlug(String slug, String authenticatedUsername) {
        HomebrewEntry entry = homebrewRepository.findBySlugAndStatus(slug, HomebrewStatus.PUBLISHED)
                .orElseThrow(() -> new ApiException("İçerik bulunamadı.", HttpStatus.NOT_FOUND));

        this.increaseViewCount(entry.getId());
        HomebrewEntryResponse response = homebrewMapper.toResponseDto(entry);

        if (authenticatedUsername != null) {
            userRepository.findByUsername(authenticatedUsername).ifPresent(user -> {
                boolean isLiked = likeRepository.existsByTargetTypeAndTargetIdAndUser_Id(
                        TargetType.HOMEBREW_ENTRY, entry.getId(), user.getId());
                response.setLiked(isLiked);
            });
        }
        return response;
    }

    @Override
    @Transactional
    public void increaseViewCount(Long id) {
        homebrewRepository.findById(id).ifPresent(entry -> {
            entry.setViewCount(entry.getViewCount() + 1);
            homebrewRepository.save(entry);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public long countPublishedHomebrewsByCategory(HomebrewCategory category) {
        return homebrewRepository.countByStatusAndCategory(HomebrewStatus.PUBLISHED, category);
    }

    // ============= HELPER METHODS =============

    private String ensureUniqueSlug(String baseSlug) {
        // Homebrew için prefix ekle
        String slug = "hb-" + baseSlug;

        // Eğer slug zaten varsa, sonuna sayı ekle
        int counter = 1;
        while (homebrewRepository.existsBySlug(slug)) {
            slug = "hb-" + baseSlug + "-" + counter++;
        }

        return slug;
    }

    private List<HomebrewEntryResponse> convertAndCheckLikes(List<HomebrewEntry> entries, String username) {
        List<HomebrewEntryResponse> responses = entries.stream()
                .map(homebrewMapper::toResponseDto)
                .collect(Collectors.toList());

        if (username != null && !responses.isEmpty()) {
            userRepository.findByUsername(username).ifPresent(user -> {
                List<Long> ids = responses.stream().map(HomebrewEntryResponse::getId).toList();
                List<Long> likedIds = likeRepository.findLikedIdsByUser(user.getId(), TargetType.HOMEBREW_ENTRY, ids);

                responses.forEach(res -> {
                    if (likedIds.contains(res.getId())) {
                        res.setLiked(true);
                    }
                });
            });
        }
        return responses;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<HomebrewEntryListResponse> getPublishedHomebrews(Pageable pageable, String authenticatedUsername) {
        Page<HomebrewEntry> entries = homebrewRepository.findAllByStatus(HomebrewStatus.PUBLISHED, pageable);
        return convertPageAndCheckLikes(entries, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomebrewEntryListResponse> getPublishedHomebrewsByCategory(
            HomebrewCategory category,
            Pageable pageable,
            String authenticatedUsername) {
        Page<HomebrewEntry> entries = homebrewRepository.findAllByStatusAndCategory(
                HomebrewStatus.PUBLISHED, category, pageable);
        return convertPageAndCheckLikes(entries, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomebrewEntryListResponse> search(
            String keyword,
            Pageable pageable,
            String authenticatedUsername) {
        Page<HomebrewEntry> entries = homebrewRepository.searchPublicPaginated(keyword, pageable);
        return convertPageAndCheckLikes(entries, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HomebrewEntryListResponse> searchByCategory(
            HomebrewCategory category,
            String keyword,
            Pageable pageable,
            String authenticatedUsername) {
        Page<HomebrewEntry> entries = homebrewRepository.searchByCategoryPaginated(
                category, keyword, pageable);
        return convertPageAndCheckLikes(entries, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HomebrewEntryResponse> getUserHomebrews(String username, String authenticatedUsername) {
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        List<HomebrewEntry> entries = homebrewRepository.findAllByAuthorIdAndStatusOrderByPublishedAtDesc(
                targetUser.getId(), HomebrewStatus.PUBLISHED);

        return convertAndCheckLikes(entries, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<HomebrewCategory, Long> getCategoryCounts() {
        return java.util.Arrays.stream(HomebrewCategory.values())
                .collect(Collectors.toMap(
                        category -> category,
                        category -> homebrewRepository.countByStatusAndCategory(
                                HomebrewStatus.PUBLISHED, category)
                ));
    }

    private Page<HomebrewEntryListResponse> convertPageAndCheckLikes(
            Page<HomebrewEntry> entriesPage,
            String username) {

        // Page içindeki her entry'yi ListResponse'a çevir
        Page<HomebrewEntryListResponse> responses = entriesPage.map(homebrewMapper::toListDto);

        // Eğer authenticated user varsa ve sonuç varsa, like kontrolü yap
        if (username != null && responses.hasContent()) {
            userRepository.findByUsername(username).ifPresent(user -> {
                // Tüm entry ID'lerini topla
                List<Long> ids = responses.getContent().stream()
                        .map(HomebrewEntryListResponse::getId)
                        .toList();

                // Bu user'ın beğendiği entry'leri bul
                List<Long> likedIds = likeRepository.findLikedIdsByUser(
                        user.getId(),
                        TargetType.HOMEBREW_ENTRY,
                        ids
                );

                // Her response'da liked flag'i set et
                responses.forEach(res -> {
                    if (likedIds.contains(res.getId())) {
                        res.setLiked(true);
                    }
                });
            });
        }

        return responses;
    }


}