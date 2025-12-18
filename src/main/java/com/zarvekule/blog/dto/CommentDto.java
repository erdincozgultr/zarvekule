package com.zarvekule.blog.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDto {
    private Long id;
    private String content;
    private UserSummaryDto user;
    private LocalDateTime createdAt;
    private boolean isDeleted;

    private List<CommentDto> replies;
}