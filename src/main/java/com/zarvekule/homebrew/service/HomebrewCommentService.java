package com.zarvekule.homebrew.service;

import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.homebrew.dto.HomebrewCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HomebrewCommentService {

    void addComment(String username, HomebrewCommentRequest request);

    Page<CommentDto> getCommentsForHomebrew(Long homebrewId, Pageable pageable);

    void deleteComment(String username, Long commentId);

    void approveComment(String username, Long commentId);

    Page<CommentDto> getPendingComments(Pageable pageable);
}