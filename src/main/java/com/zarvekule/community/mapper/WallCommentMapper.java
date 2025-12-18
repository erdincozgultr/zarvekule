package com.zarvekule.community.mapper;

import com.zarvekule.community.dto.WallCommentResponse;
import com.zarvekule.community.entity.WallComment;
import com.zarvekule.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WallCommentMapper {

    private final UserMapper userMapper;

    public WallCommentResponse toResponseDto(WallComment comment) {
        if (comment == null) return null;

        WallCommentResponse dto = new WallCommentResponse();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setLikeCount(comment.getLikeCount());

        dto.setAuthor(userMapper.toSummaryDto(comment.getAuthor()));

        return dto;
    }
}