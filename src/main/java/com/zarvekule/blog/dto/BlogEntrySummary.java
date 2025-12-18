package com.zarvekule.blog.dto;

import com.zarvekule.blog.enums.BlogCategory;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BlogEntrySummary {
    private Long id;
    private String title;
    private String slug;
    private String thumbnailUrl;
    private BlogStatus status;

    // Listede görünecek yeni bilgiler
    private BlogCategory category;
    private int readingTime;

    private long viewCount;
    private long likeCount;
    private LocalDateTime publishedAt;

    private UserSummaryDto author;
}