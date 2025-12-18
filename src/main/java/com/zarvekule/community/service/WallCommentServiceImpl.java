package com.zarvekule.community.service;

import com.zarvekule.community.dto.WallCommentRequest;
import com.zarvekule.community.dto.WallCommentResponse;
import com.zarvekule.community.entity.WallComment;
import com.zarvekule.community.mapper.WallCommentMapper;
import com.zarvekule.community.repository.WallCommentRepository;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WallCommentServiceImpl implements WallCommentService {

    private final WallCommentRepository commentRepository;
    private final UserRepository userRepository;
    private final WallCommentMapper commentMapper;
    private final GamificationService gamificationService;

    @Override
    @Transactional
    public WallCommentResponse postComment(String authenticatedUsername, WallCommentRequest request) {
        User author = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Oturum açmış kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED));

        User profileOwner = userRepository.findByUsername(request.getProfileOwnerUsername())
                .orElseThrow(() -> new ApiException("Hedef profil bulunamadı: " + request.getProfileOwnerUsername(), HttpStatus.NOT_FOUND));

        WallComment comment = new WallComment();
        comment.setContent(request.getContent());
        comment.setAuthor(author);
        comment.setProfileOwner(profileOwner);

        WallComment savedComment = commentRepository.save(comment);
        gamificationService.processAction(author, ActionType.POST_COMMENT);
        return commentMapper.toResponseDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WallCommentResponse> getCommentsByProfileOwner(String profileOwnerUsername, Pageable pageable) {
        User profileOwner = userRepository.findByUsername(profileOwnerUsername)
                .orElseThrow(() -> new ApiException("Profil bulunamadı: " + profileOwnerUsername, HttpStatus.NOT_FOUND));

        Page<WallComment> commentsPage = commentRepository.findByProfileOwner_IdOrderByCreatedAtDesc(profileOwner.getId(), pageable);

        return commentsPage.map(commentMapper::toResponseDto);
    }

    @Override
    @Transactional
    public void deleteComment(String authenticatedUsername, Long commentId) {

        WallComment comment = commentRepository.findByIdWithAuthorAndOwner(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı. ID: " + commentId, HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findByUsername(authenticatedUsername)
                .orElseThrow(() -> new ApiException("Geçerli kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED));

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isProfileOwner = comment.getProfileOwner().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));


        if (!isAuthor && !isProfileOwner && !isAdmin) {
            throw new ApiException("Bu yorumu silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        commentRepository.delete(comment);
    }
}