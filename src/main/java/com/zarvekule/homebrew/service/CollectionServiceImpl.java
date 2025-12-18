package com.zarvekule.homebrew.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.homebrew.dto.CollectionDto;
import com.zarvekule.homebrew.dto.CollectionRequest;
import com.zarvekule.homebrew.entity.HomebrewCollection;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.homebrew.mapper.CollectionMapper;
import com.zarvekule.homebrew.repository.HomebrewCollectionRepository;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final HomebrewCollectionRepository collectionRepository;
    private final HomebrewEntryRepository entryRepository;
    private final UserRepository userRepository;
    private final CollectionMapper collectionMapper;

    @Override
    @Transactional
    public CollectionDto create(String username, CollectionRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        HomebrewCollection collection = new HomebrewCollection();
        collection.setName(request.getName());
        collection.setDescription(request.getDescription());
        collection.setPublic(request.isPublic());
        collection.setOwner(owner);
        collection.setCreatedAt(LocalDateTime.now());

        collection = collectionRepository.save(collection);
        return collectionMapper.toDto(collection);
    }

    @Override
    @Transactional
    public void delete(String username, Long collectionId) {
        HomebrewCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyonu silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        collectionRepository.delete(collection);
    }

    @Override
    @Transactional
    public void addEntryToCollection(String username, Long collectionId, Long entryId) {
        HomebrewCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyona ekleme yapma yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        HomebrewEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ApiException("İçerik bulunamadı.", HttpStatus.NOT_FOUND));

        boolean isPublished = entry.getStatus() == HomebrewStatus.PUBLISHED;
        boolean isMine = entry.getAuthor().getUsername().equals(username);

        if (!isPublished && !isMine) {
            throw new ApiException("Bu içeriği koleksiyona ekleyemezsiniz (Yayında değil).", HttpStatus.BAD_REQUEST);
        }

        collection.getEntries().add(entry);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional
    public void removeEntryFromCollection(String username, Long collectionId, Long entryId) {
        HomebrewCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyonu düzenleme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        HomebrewEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new ApiException("İçerik bulunamadı.", HttpStatus.NOT_FOUND));

        if (collection.getEntries().contains(entry)) {
            collection.getEntries().remove(entry);
            collectionRepository.save(collection);
        } else {
            throw new ApiException("Bu içerik zaten koleksiyonda yok.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CollectionDto getById(Long id) {
        HomebrewCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        return collectionMapper.toDto(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDto> getMyCollections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        return collectionMapper.toDtoList(collectionRepository.findAllByOwner_Id(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollectionDto> getPublicCollectionsByUser(Long userId) {
        return collectionMapper.toDtoList(collectionRepository.findAllByOwner_IdAndIsPublicTrue(userId));
    }
}