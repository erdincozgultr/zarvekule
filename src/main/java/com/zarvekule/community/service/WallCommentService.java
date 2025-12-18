package com.zarvekule.community.service;

import com.zarvekule.community.dto.WallCommentRequest;
import com.zarvekule.community.dto.WallCommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WallCommentService {


    WallCommentResponse postComment(String authenticatedUsername, WallCommentRequest request);

    Page<WallCommentResponse> getCommentsByProfileOwner(String profileOwnerUsername, Pageable pageable);

    void deleteComment(String authenticatedUsername, Long commentId);
}