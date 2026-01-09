package com.zarvekule.wiki.service;

import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.exceptions.ApiException;
import com.zarvekule.gamification.enums.ActionType;
import com.zarvekule.gamification.service.GamificationService;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.user.repository.UserRepository;
import com.zarvekule.wiki.dto.WikiCommentRequest;
import com.zarvekule.wiki.entity.WikiComment;
import com.zarvekule.wiki.entity.WikiEntry;
import com.zarvekule.wiki.repository.WikiCommentRepository;
import com.zarvekule.wiki.repository.WikiEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class WikiCommentServiceImpl implements WikiCommentService {

    private final WikiCommentRepository commentRepository;
    private final WikiEntryRepository wikiRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final GamificationService gamificationService; // ✨ ROZET SİSTEMİ

    @Override
    @Transactional
    public void addComment(String username, WikiCommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        WikiEntry wiki = wikiRepository.findById(request.getWikiId())
                .orElseThrow(() -> new ApiException("Wiki bulunamadı.", HttpStatus.NOT_FOUND));

        WikiComment comment = new WikiComment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setWiki(wiki);

        // Moderatör veya Admin ise yorumu otomatik onayla
        boolean isPrivileged = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_MODERATOR"));

        comment.setApproved(true);

        commentRepository.save(comment);

        // ✨ ROZET TETİKLE - Yorum yapıldığında
        gamificationService.processAction(user, ActionType.POST_COMMENT);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsForWiki(Long wikiId, Pageable pageable) {
        return commentRepository.findByWiki_IdAndIsApprovedTrue(wikiId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public void deleteComment(String username, Long commentId) {
        WikiComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı.", HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED));

        boolean isOwner = Objects.equals(comment.getUser().getUsername(), username);

        // Admin veya Moderatör ise silebilir
        boolean isPrivileged = currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_MODERATOR"));

        if (!isOwner && !isPrivileged) {
            throw new ApiException("Bu yorumu silme yetkiniz yok.", HttpStatus.FORBIDDEN);
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void approveComment(String username, Long commentId) {
        WikiComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı.", HttpStatus.NOT_FOUND));

        comment.setApproved(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getPendingComments(Pageable pageable) {
        return commentRepository.findByIsApprovedFalse(pageable)
                .map(this::mapToDto);
    }

    private CommentDto mapToDto(WikiComment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUser(userMapper.toSummaryDto(comment.getUser()));
        return dto;
    }
}