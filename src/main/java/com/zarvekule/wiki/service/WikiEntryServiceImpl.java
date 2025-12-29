package com.zarvekule.wiki.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zarvekule.community.enums.TargetType;
import com.zarvekule.community.repository.LikeEntryRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.util.SlugUtils;
import com.zarvekule.wiki.dto.WikiBulkImportRequest;
import com.zarvekule.wiki.dto.WikiEntryListResponse;
import com.zarvekule.wiki.dto.WikiEntryRequest;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import com.zarvekule.wiki.mapper.WikiEntryMapper;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WikiEntryServiceImpl implements WikiEntryService {

    private final WikiEntryRepository wikiRepository;
    private final WikiEntryMapper mapper;
    private final UserRepository userRepository;
    private final LikeEntryRepository likeRepository;
    private final ObjectMapper objectMapper;

    // === CRUD ===

    @Override
    @Transactional
    public WikiEntryResponse create(String username, WikiEntryRequest request) {
        User author = getUser(username);

        WikiEntry entry = new WikiEntry();
        entry.setTitle(request.getTitle());
        entry.setCategory(request.getCategory());
        entry.setMetadata(request.getMetadata());
        entry.setTurkishContent(request.getTurkishContent());
        entry.setSourceKey(request.getSourceKey());
        entry.setImageUrl(request.getImageUrl());
        entry.setAuthor(author);
        entry.setStatus(request.getStatus() != null ? request.getStatus() : WikiStatus.PUBLISHED);
        entry.setSlug(createUniqueSlug(request.getCustomSlug(), request.getTitle()));

        WikiEntry saved = wikiRepository.save(entry);
        log.info("Wiki oluşturuldu: {} [{}]", saved.getTitle(), saved.getCategory());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public WikiEntryResponse update(Long id, WikiEntryRequest request, String username) {
        WikiEntry entry = getEntry(id);
        checkPermission(entry, username);

        if (request.getTitle() != null) {
            entry.setTitle(request.getTitle());
            entry.setSlug(createUniqueSlug(null, request.getTitle()));
        }
        if (request.getCategory() != null) entry.setCategory(request.getCategory());
        if (request.getMetadata() != null) entry.setMetadata(request.getMetadata());
        if (request.getTurkishContent() != null) entry.setTurkishContent(request.getTurkishContent());
        if (request.getSourceKey() != null) entry.setSourceKey(request.getSourceKey());
        if (request.getImageUrl() != null) entry.setImageUrl(request.getImageUrl());
        if (request.getStatus() != null) entry.setStatus(request.getStatus());

        return mapper.toDto(wikiRepository.save(entry));
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        WikiEntry entry = getEntry(id);
        wikiRepository.delete(entry);
        log.info("Wiki silindi: {}", entry.getTitle());
    }

    // === DETAY ===

    @Override
    @Transactional(readOnly = true)
    public WikiEntryResponse getById(Long id, String username) {
        WikiEntry entry = getEntry(id);
        return enrichWithLike(mapper.toDto(entry), username);
    }

    @Override
    @Transactional
    public WikiEntryResponse getBySlug(String slug, String username) {
        WikiEntry entry = wikiRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException("Wiki bulunamadı", HttpStatus.NOT_FOUND));

        wikiRepository.incrementViewCount(entry.getId());

        return enrichWithLike(mapper.toDto(entry), username);
    }

    // === LİSTE (PAGINATION) ===

    @Override
    @Transactional(readOnly = true)
    public Page<WikiEntryListResponse> getAll(Pageable pageable, String username) {
        Page<WikiEntry> page = wikiRepository.findAllByStatus(WikiStatus.PUBLISHED, pageable);
        return toListPage(page, pageable, username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WikiEntryListResponse> getByCategory(ContentCategory category, Pageable pageable, String username) {
        Page<WikiEntry> page = wikiRepository.findAllByStatusAndCategory(WikiStatus.PUBLISHED, category, pageable);
        return toListPage(page, pageable, username);
    }

    // === ARAMA ===

    @Override
    @Transactional(readOnly = true)
    public Page<WikiEntryListResponse> search(String keyword, Pageable pageable, String username) {
        Page<WikiEntry> page = wikiRepository.searchByTitle(WikiStatus.PUBLISHED, keyword, pageable);
        return toListPage(page, pageable, username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WikiEntryListResponse> searchByCategory(ContentCategory category, String keyword,
                                                        Pageable pageable, String username) {
        Page<WikiEntry> page = wikiRepository.searchByCategoryAndTitle(WikiStatus.PUBLISHED, category, keyword, pageable);
        return toListPage(page, pageable, username);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WikiEntryListResponse> searchTurkish(String keyword, Pageable pageable, String username) {
        Page<WikiEntry> page = wikiRepository.searchTurkishTitle(keyword, pageable);
        return toListPage(page, pageable, username);
    }

    // === İSTATİSTİK ===

    @Override
    @Transactional(readOnly = true)
    public Map<ContentCategory, Long> getCategoryCounts() {
        Map<ContentCategory, Long> counts = new HashMap<>();
        for (ContentCategory cat : ContentCategory.values()) {
            counts.put(cat, 0L);
        }

        List<Object[]> results = wikiRepository.countByCategory();
        for (Object[] row : results) {
            counts.put((ContentCategory) row[0], (Long) row[1]);
        }

        return counts;
    }

    @Override
    @Transactional(readOnly = true)
    public long getCategoryCount(ContentCategory category) {
        return wikiRepository.countByStatusAndCategory(WikiStatus.PUBLISHED, category);
    }

    // === IMPORT ===

    @Override
    @Transactional
    public WikiEntryResponse importEntry(WikiBulkImportRequest request, String username) {
        if (request.getSourceKey() != null && wikiRepository.existsBySourceKey(request.getSourceKey())) {
            throw new ApiException("Bu içerik zaten mevcut: " + request.getSourceKey(), HttpStatus.CONFLICT);
        }

        User author = getUser(username);
        Map<String, Object> metadata = parseJson(request.getMetadata());
        Map<String, Object> turkishContent = parseJson(request.getTurkishContent());

        String title = extractTitle(metadata, turkishContent);

        WikiEntry entry = new WikiEntry();
        entry.setTitle(title);
        entry.setCategory(request.getCategory());
        entry.setMetadata(metadata);
        entry.setTurkishContent(turkishContent);
        entry.setSourceKey(request.getSourceKey());
        entry.setAuthor(author);
        entry.setStatus(WikiStatus.PUBLISHED);
        entry.setSlug(createUniqueSlug(null, title));

        WikiEntry saved = wikiRepository.save(entry);
        log.debug("Import: {} [{}]", saved.getTitle(), request.getSourceKey());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public int bulkImport(List<WikiBulkImportRequest> requests, String username) {
        int success = 0;
        for (WikiBulkImportRequest req : requests) {
            try {
                importEntry(req, username);
                success++;
            } catch (Exception e) {
                log.warn("Import hatası [{}]: {}", req.getSourceKey(), e.getMessage());
            }
        }
        log.info("Bulk import: {}/{}", success, requests.size());
        return success;
    }

    // === YARDIMCI METODLAR ===

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));
    }

    private WikiEntry getEntry(Long id) {
        return wikiRepository.findById(id)
                .orElseThrow(() -> new ApiException("Wiki bulunamadı", HttpStatus.NOT_FOUND));
    }

    private void checkPermission(WikiEntry entry, String username) {
        User user = getUser(username);
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && entry.getAuthor() != null &&
                !entry.getAuthor().getUsername().equals(username)) {
            throw new ApiException("Yetkiniz yok", HttpStatus.FORBIDDEN);
        }
    }

    private String createUniqueSlug(String customSlug, String title) {
        String slug = customSlug != null ?
                SlugUtils.createSlug(customSlug, null) :
                SlugUtils.createSlug(null, title);

        String finalSlug = slug;
        int count = 1;
        while (wikiRepository.existsBySlug(finalSlug)) {
            finalSlug = slug + "-" + count++;
        }
        return finalSlug;
    }

    private WikiEntryResponse enrichWithLike(WikiEntryResponse dto, String username) {
        if (username != null) {
            userRepository.findByUsername(username).ifPresent(user -> {
                boolean liked = likeRepository.existsByTargetTypeAndTargetIdAndUser_Id(
                        TargetType.WIKI_ENTRY, dto.getId(), user.getId());
                dto.setLiked(liked);
            });
        }
        return dto;
    }

    private Page<WikiEntryListResponse> toListPage(Page<WikiEntry> page, Pageable pageable, String username) {
        List<WikiEntryListResponse> content = page.getContent().stream()
                .map(mapper::toListDto)
                .collect(Collectors.toList());

        // Like kontrolü
        if (username != null && !content.isEmpty()) {
            userRepository.findByUsername(username).ifPresent(user -> {
                List<Long> ids = content.stream().map(WikiEntryListResponse::getId).toList();
                List<Long> likedIds = likeRepository.findLikedIdsByUser(user.getId(), TargetType.WIKI_ENTRY, ids);
                content.forEach(item -> item.setLiked(likedIds.contains(item.getId())));
            });
        }

        return new PageImpl<>(content, pageable, page.getTotalElements());
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.warn("JSON parse hatası: {}", e.getMessage());
            return null;
        }
    }

    private String extractTitle(Map<String, Object> metadata, Map<String, Object> turkishContent) {
        if (turkishContent != null && turkishContent.containsKey("name")) {
            return turkishContent.get("name").toString();
        }
        if (metadata != null && metadata.containsKey("name")) {
            return metadata.get("name").toString();
        }
        return "Untitled";
    }
}
