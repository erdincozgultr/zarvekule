package com.zarvekule.blog.service;

import com.zarvekule.exceptions.ApiException;
import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.blog.dto.CommentRequest;
import com.zarvekule.blog.entity.BlogComment;
import com.zarvekule.blog.entity.BlogEntry;
import com.zarvekule.blog.repository.BlogCommentRepository;
import com.zarvekule.blog.repository.BlogEntryRepository;
import com.zarvekule.user.entity.User;
import com.zarvekule.user.mapper.UserMapper;
import com.zarvekule.user.repository.UserRepository;
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
public class BlogCommentServiceImpl implements BlogCommentService {

    private final BlogCommentRepository commentRepository;
    private final BlogEntryRepository blogRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public void addComment(String username, CommentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.NOT_FOUND));

        BlogEntry blog = blogRepository.findById(request.getBlogId())
                .orElseThrow(() -> new ApiException("Blog yazısı bulunamadı.", HttpStatus.NOT_FOUND));

        BlogComment comment = new BlogComment();
        comment.setContent(request.getContent());
        comment.setUser(user);
        comment.setBlog(blog);

        // GÜNCELLEME: Moderatör veya Admin ise yorumu otomatik onaylanır
        boolean isPrivileged = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_MODERATOR"));

        comment.setApproved(isPrivileged);

        commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getCommentsForBlog(Long blogId, Pageable pageable) {
        return commentRepository.findByBlog_IdAndIsApprovedTrue(blogId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public void deleteComment(String username, Long commentId) {
        BlogComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ApiException("Yorum bulunamadı.", HttpStatus.NOT_FOUND));

        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("Kullanıcı bulunamadı.", HttpStatus.UNAUTHORIZED));

        boolean isOwner = Objects.equals(comment.getUser().getUsername(), username);

        // GÜNCELLEME: Admin veya Moderatör ise silebilir
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
        // Yetki kontrolü Controller katmanında @PreAuthorize ile yapıldı
        BlogComment comment = commentRepository.findById(commentId)
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

    private CommentDto mapToDto(BlogComment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUser(userMapper.toSummaryDto(comment.getUser()));
        return dto;
    }
}