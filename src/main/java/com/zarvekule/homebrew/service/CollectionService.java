package com.zarvekule.homebrew.service;

import com.zarvekule.homebrew.dto.CollectionDto;
import com.zarvekule.homebrew.dto.CollectionRequest;

import java.util.List;

public interface CollectionService {

    CollectionDto create(String username, CollectionRequest request);

    void delete(String username, Long collectionId);

    void addEntryToCollection(String username, Long collectionId, Long entryId);

    void removeEntryFromCollection(String username, Long collectionId, Long entryId);

    CollectionDto getById(Long id);

    List<CollectionDto> getMyCollections(String username);

    List<CollectionDto> getPublicCollectionsByUser(Long userId);
}