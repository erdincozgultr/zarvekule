package com.zarvekule.wiki.service;

import com.zarvekule.community.enums.TargetType;
import com.zarvekule.community.repository.LikeEntryRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.util.SlugUtils;
import com.zarvekule.wiki.dto.WikiEntryRequest;
import com.zarvekule.wiki.dto.WikiEntryResponse;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import com.zarvekule.wiki.mapper.WikiEntryMapper;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WikiEntryServiceImpl implements WikiEntryService {

    private final WikiEntryRepository wikiRepository;
    private final WikiEntryMapper wikiMapper;
    private final UserRepository userRepository;
    private final LikeEntryRepository likeRepository;

    @Override
    @Transactional
    public WikiEntryResponse create(String authenticatedUsername, WikiEntryRequest request) {
        var author = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı", HttpStatus.NOT_FOUND));

        WikiEntry entry = new WikiEntry();
        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setCategory(request.getCategory());
        entry.setAuthor(author);
        entry.setStatus(WikiStatus.PUBLISHED);
        entry.setImageUrl(request.getImageUrl()); // Resim set ediliyor

        String slug = SlugUtils.createSlug(null, request.getTitle());
        entry.setSlug(slug); // Unique kontrol eklenebilir

        return wikiMapper.toDto(wikiRepository.save(entry));
    }

    @Override
    @Transactional
    public WikiEntryResponse update(Long id, WikiEntryRequest request, String authenticatedUsername) {
        WikiEntry entry = wikiRepository.findById(id)
                .orElseThrow(() -> new ApiException("Wiki bulunamadı", HttpStatus.NOT_FOUND));

        // Basit yetki kontrolü
        if (!entry.getAuthor().getUsername().equals(authenticatedUsername)) {
            // Admin kontrolü de eklenebilir
            // throw new ApiException("Yetkisiz işlem", HttpStatus.FORBIDDEN);
        }

        entry.setTitle(request.getTitle());
        entry.setContent(request.getContent());
        entry.setCategory(request.getCategory());
        if(request.getImageUrl() != null) {
            entry.setImageUrl(request.getImageUrl());
        }

        return wikiMapper.toDto(wikiRepository.save(entry));
    }

    @Override
    @Transactional
    public void delete(Long id, String authenticatedUsername) {
        WikiEntry entry = wikiRepository.findById(id)
                .orElseThrow(() -> new ApiException("Wiki bulunamadı", HttpStatus.NOT_FOUND));
        wikiRepository.delete(entry);
    }

    // --- OKUMA VE LİSTELEME İŞLEMLERİ (Like Entegrasyonu) ---

    @Override
    @Transactional(readOnly = true)
    public WikiEntryResponse getBySlug(String slug, String authenticatedUsername) {
        WikiEntry entry = wikiRepository.findBySlug(slug);
        if (entry == null) throw new ApiException("Wiki bulunamadı", HttpStatus.NOT_FOUND);

        WikiEntryResponse response = wikiMapper.toDto(entry);

        // Tekil beğeni kontrolü
        if (authenticatedUsername != null) {
            userRepository.findByUsername(authenticatedUsername).ifPresent(user -> {
                boolean isLiked = likeRepository.existsByTargetTypeAndTargetIdAndUser_Id(
                        TargetType.WIKI_ENTRY, entry.getId(), user.getId());
                response.setLiked(isLiked);
            });
        }
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<WikiEntryResponse> getPublishedEntries(String authenticatedUsername) {
        List<WikiEntry> entries = wikiRepository.findAllByStatus(WikiStatus.PUBLISHED);
        List<WikiEntryResponse> responses = wikiMapper.toDtoList(entries);
        return checkLikesForList(responses, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WikiEntryResponse> getPublishedEntriesByCategory(ContentCategory category, String authenticatedUsername) {
        List<WikiEntry> entries = wikiRepository.findAllByStatusAndCategory(WikiStatus.PUBLISHED, category);
        List<WikiEntryResponse> responses = wikiMapper.toDtoList(entries);
        return checkLikesForList(responses, authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WikiEntryResponse> search(String keyword, String authenticatedUsername) {
        List<WikiEntry> entries = wikiRepository.searchPublic(keyword);
        List<WikiEntryResponse> responses = wikiMapper.toDtoList(entries);
        return checkLikesForList(responses, authenticatedUsername);
    }

    // Yardımcı Metod: Listeler için toplu beğeni kontrolü
    private List<WikiEntryResponse> checkLikesForList(List<WikiEntryResponse> responses, String username) {
        if (username != null && !responses.isEmpty()) {
            userRepository.findByUsername(username).ifPresent(user -> {
                List<Long> ids = responses.stream().map(WikiEntryResponse::getId).toList();

                // LikeEntryRepository'deki düzeltilmiş metod (l.user.id) kullanılıyor
                List<Long> likedIds = likeRepository.findLikedIdsByUser(user.getId(), TargetType.WIKI_ENTRY, ids);

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