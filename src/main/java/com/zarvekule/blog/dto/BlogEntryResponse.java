package com.zarvekule.blog.dto;

import com.zarvekule.blog.enums.BlogCategory;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BlogEntryResponse {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String thumbnailUrl;
    private BlogStatus status;

    // Yeni Alanlar
    private BlogCategory category;
    private int readingTime;
    private String seoTitle;
    private String seoDescription;

    private long viewCount;
    private long likeCount;

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    private Set<String> tags;

    private UserSummaryDto author;
}