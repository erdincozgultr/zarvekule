package com.zarvekule.wiki.service;

import com.zarvekule.blog.dto.CommentDto;
import com.zarvekule.wiki.dto.WikiCommentRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WikiCommentService {

    void addComment(String username, WikiCommentRequest request);

    Page<CommentDto> getCommentsForWiki(Long wikiId, Pageable pageable);

    void deleteComment(String username, Long commentId);

    void approveComment(String username, Long commentId);

    Page<CommentDto> getPendingComments(Pageable pageable);
}