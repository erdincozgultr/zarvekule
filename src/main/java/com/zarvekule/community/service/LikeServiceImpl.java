package com.zarvekule.community.service;

import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.community.dto.LikeRequest;
import com.zarvekule.community.entity.LikeEntry;
import com.zarvekule.community.enums.TargetType;
import com.zarvekule.community.entity.WallComment;
import com.zarvekule.community.repository.LikeEntryRepository;
import com.zarvekule.community.repository.WallCommentRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.homebrew.entity.HomebrewEntry;
import com.zarvekule.homebrew.repository.HomebrewEntryRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeEntryRepository likeRepository;
    private final UserRepository userRepository;
    private final WikiEntryRepository wikiRepository;
    private final HomebrewEntryRepository homebrewRepository;
    private final BlogEntryRepository blogRepository;
    private final WallCommentRepository wallCommentRepository;

    @Override
    @Transactional
    public boolean toggleLike(String authenticatedUsername, LikeRequest request) {
        User user = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Kullanıcı oturumu bulunamadı.", HttpStatus.UNAUTHORIZED));

        TargetType type = request.getTargetType();
        Long targetId = request.getTargetId();

        // BURASI GÜNCELLENDİ: Metod adı repository ile uyumlu hale getirildi
        Optional<LikeEntry> existingLike = likeRepository.findByUser_IdAndTargetTypeAndTargetId(
                user.getId(), type, targetId);

        boolean isLiked;

        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            isLiked = false;
        } else {
            LikeEntry newLike = new LikeEntry();
            newLike.setUser(user);
            newLike.setTargetType(type);
            newLike.setTargetId(targetId);
            likeRepository.save(newLike);
            isLiked = true;
        }

        long newCount = likeRepository.countByTargetTypeAndTargetId(type, targetId);
        updateTargetEntityLikeCount(type, targetId, newCount);

        return isLiked;
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(TargetType targetType, Long targetId) {
        return likeRepository.countByTargetTypeAndTargetId(targetType, targetId);
    }

    @Override
    @Transactional
    public void updateTargetEntityLikeCount(TargetType targetType, Long targetId, long newCount) {
        switch (targetType) {
            case BLOG_ENTRY:
                BlogEntry blog = blogRepository.findById(targetId)
                        .orElseThrow(() -> new ApiException("Blog bulunamadı.", HttpStatus.NOT_FOUND));
                blog.setLikeCount(newCount);
                blogRepository.save(blog);
                break;

            case WALL_COMMENT:
                WallComment comment = wallCommentRepository.findById(targetId)
                        .orElseThrow(() -> new ApiException("Yorum bulunamadı.", HttpStatus.NOT_FOUND));
                comment.setLikeCount((int) newCount);
                wallCommentRepository.save(comment);
                break;

            case WIKI_ENTRY:
                WikiEntry wiki = wikiRepository.findById(targetId)
                        .orElseThrow(() -> new ApiException("Wiki girdisi bulunamadı.", HttpStatus.NOT_FOUND));
                wiki.setLikeCount(newCount);
                wikiRepository.save(wiki);
                break;

            case HOMEBREW_ENTRY:
                HomebrewEntry homebrew = homebrewRepository.findById(targetId)
                        .orElseThrow(() -> new ApiException("Homebrew içeriği bulunamadı.", HttpStatus.NOT_FOUND));
                homebrew.setLikeCount(newCount);
                homebrewRepository.save(homebrew);
                break;


            default:
                throw new ApiException("Geçersiz beğeni hedef tipi.", HttpStatus.BAD_REQUEST);
        }
    }
}