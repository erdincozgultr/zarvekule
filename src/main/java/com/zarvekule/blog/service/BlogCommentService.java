package com.zarvekule.blog.service;

import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.blog.dto.CommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogCommentService {

    void addComment(String username, CommentRequest request);

    Page<CommentDto> getCommentsForBlog(Long blogId, Pageable pageable);

    void deleteComment(String username, Long commentId);

    // --- YENİ METODLAR ---
    void approveComment(String username, Long commentId); // Admin onayı

    Page<CommentDto> getPendingComments(Pageable pageable); // Onay bekleyenler
}