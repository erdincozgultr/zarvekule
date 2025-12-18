package com.zarvekule.wiki.entity;

import com.zarvekule.user.entity.User;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "wiki_entries")
@Data
public class WikiEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String imageUrl; // Resim alanı

    @Enumerated(EnumType.STRING)
    private ContentCategory category;

    @Enumerated(EnumType.STRING)
    private WikiStatus status = WikiStatus.PUBLISHED;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // Like sistemi için gerekli alan
    @Column(name = "like_count")
    private long likeCount = 0;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}