package com.zarvekule.wiki.service;

import com.zarvekule.wiki.dto.WikiCollectionDto;
import com.zarvekule.wiki.dto.WikiCollectionRequest;

import java.util.List;

public interface WikiCollectionService {
    WikiCollectionDto create(String username, WikiCollectionRequest request);

    void delete(String username, Long collectionId);

    void addEntryToCollection(String username, Long collectionId, Long wikiId);

    void removeEntryFromCollection(String username, Long collectionId, Long wikiId);

    WikiCollectionDto getById(Long id);

    List<WikiCollectionDto> getMyCollections(String username);

    List<WikiCollectionDto> getPublicCollectionsByUser(Long userId);
}