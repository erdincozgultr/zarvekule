package com.zarvekule.blog.service;

import com.zarvekule.blog.dto.BlogCollectionDto;
import com.zarvekule.blog.dto.BlogCollectionRequest;
import com.zarvekule.blog.entity.BlogCollection;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.blog.mapper.BlogCollectionMapper;
import com.zarvekule.blog.repository.BlogCollectionRepository;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.exceptions.ApiException;
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
public class BlogCollectionServiceImpl implements BlogCollectionService {

    private final BlogCollectionRepository collectionRepository;
    private final BlogEntryRepository blogRepository;
    private final UserRepository userRepository;
    private final BlogCollectionMapper collectionMapper;

    @Override
    @Transactional
    public BlogCollectionDto create(String username, BlogCollectionRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        BlogCollection collection = new BlogCollection();
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
        BlogCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyonu silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        collectionRepository.delete(collection);
    }

    @Override
    @Transactional
    public void addEntryToCollection(String username, Long collectionId, Long blogId) {
        BlogCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyona ekleme yapma yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        BlogEntry blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ApiException("Blog yazısı bulunamadı.", HttpStatus.NOT_FOUND));

        // Sadece yayınlanmış bloglar veya kendi blogları eklenebilir
        boolean isPublished = blog.getStatus() == BlogStatus.PUBLISHED;
        boolean isMine = blog.getAuthor().getUsername().equals(username);

        if (!isPublished && !isMine) {
            throw new ApiException("Bu blog yazısını koleksiyona ekleyemezsiniz (Yayında değil).", HttpStatus.BAD_REQUEST);
        }

        if (collection.getEntries().contains(blog)) {
            throw new ApiException("Bu blog zaten koleksiyonda.", HttpStatus.BAD_REQUEST);
        }

        collection.getEntries().add(blog);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional
    public void removeEntryFromCollection(String username, Long collectionId, Long blogId) {
        BlogCollection collection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getOwner().getUsername().equals(username)) {
            throw new ApiException("Bu koleksiyonu düzenleme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        BlogEntry blog = blogRepository.findById(blogId)
                .orElseThrow(() -> new ApiException("Blog yazısı bulunamadı.", HttpStatus.NOT_FOUND));

        if (!collection.getEntries().contains(blog)) {
            throw new ApiException("Bu blog zaten koleksiyonda yok.", HttpStatus.BAD_REQUEST);
        }

        collection.getEntries().remove(blog);
        collectionRepository.save(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public BlogCollectionDto getById(Long id) {
        BlogCollection collection = collectionRepository.findById(id)
                .orElseThrow(() -> new ApiException("Koleksiyon bulunamadı.", HttpStatus.NOT_FOUND));

        return collectionMapper.toDto(collection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogCollectionDto> getMyCollections(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        return collectionMapper.toDtoList(collectionRepository.findAllByOwner_Id(user.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlogCollectionDto> getPublicCollectionsByUser(Long userId) {
        return collectionMapper.toDtoList(collectionRepository.findAllByOwner_IdAndIsPublicTrue(userId));
    }
}