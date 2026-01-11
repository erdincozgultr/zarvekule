package com.zarvekule.blog.service;

import com.zarvekule.blog.dto.BlogCollectionDto;
import com.zarvekule.blog.dto.BlogCollectionRequest;

import java.util.List;

public interface BlogCollectionService {
    BlogCollectionDto create(String username, BlogCollectionRequest request);

    void delete(String username, Long collectionId);

    void addEntryToCollection(String username, Long collectionId, Long blogId);

    void removeEntryFromCollection(String username, Long collectionId, Long blogId);

    BlogCollectionDto getById(Long id);

    List<BlogCollectionDto> getMyCollections(String username);

    List<BlogCollectionDto> getPublicCollectionsByUser(Long userId);
}