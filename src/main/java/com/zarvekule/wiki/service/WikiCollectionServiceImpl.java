package com.zarvekule.wiki.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.wiki.dto.WikiCollectionDto;
import com.zarvekule.wiki.dto.WikiCollectionRequest;
import com.zarvekule.wiki.entity.WikiCollection;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.mapper.WikiCollectionMapper;
import com.zarvekule.wiki.repository.WikiCollectionRepository;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WikiCollectionServiceImpl implements WikiCollectionService {

    private final WikiCollectionRepository collectionRepository;
    private final WikiEntryRepository wikiRepository;
    private final UserRepository userRepository;
    private final WikiCollectionMapper collectionMapper;

    @Override
    @Transactional
    public WikiCollectionDto create(String username, WikiCollectionRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        WikiCollection collection = new WikiCollection();
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
        WikiCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyonu silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        collectionRepository.delete(collection);
    }

    @Override
    @Transactional
    public void addEntryToCollection(String username, Long collectionId, Long wikiId) {
        WikiCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyona ekleme yapma yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        WikiEntry wiki = wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ApiException("Wiki içeriği bulunamadı.", HttpStatus.NOT_FOUND));

        // Wiki içerikleri genelde herkese açık, ama yine de kontrol edebiliriz
        if (collection.getEntries().contains(wiki)) {
            throw new ApiException("Bu içerik zaten koleksiyonda.", HttpStatus.BAD_REQUEST);
        }

        collection.getEntries().add(wiki);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional
    public void removeEntryFromCollection(String username, Long collectionId, Long wikiId) {
        WikiCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyonu düzenleme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        WikiEntry wiki = wikiRepository.findById(wikiId)
                .orElseThrow(() -> new ApiException("Wiki içeriği bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getEntries().contains(wiki)) {
            throw new ApiException("Bu içerik zaten koleksiyonda yok.", HttpStatus.BAD_REQUEST);
        }

        collection.getEntries().remove(wiki);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public WikiCollectionDto getById(Long id) {
        WikiCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        return collectionMapper.toDto(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WikiCollectionDto> getMyCollections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        return collectionMapper.toDtoList(collectionRepository.findAllByOwner_Id(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WikiCollectionDto> getPublicCollectionsByUser(Long userId) {
        return collectionMapper.toDtoList(collectionRepository.findAllByOwner_IdAndIsPublicTrue(userId));
    }
}