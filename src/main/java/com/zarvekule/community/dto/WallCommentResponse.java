package com.zarvekule.community.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WallCommentResponse {
    private Long id;
    private String content;

    private UserSummaryDto author;

    private LocalDateTime createdAt;
    private int likeCount;
}