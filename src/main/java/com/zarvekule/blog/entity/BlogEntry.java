package com.zarvekule.blog.entity;

import com.zarvekule.blog.enums.BlogCategory;
import com.zarvekule.blog.enums.BlogStatus;
import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "blog_entries")
@Getter
@Setter
public class BlogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlogStatus status = BlogStatus.DRAFT;

    // --- YENİ EKLENEN ALANLAR ---

    @Enumerated(EnumType.STRING)
    private BlogCategory category; // Kategori

    private int readingTime; // Dakika cinsinden okuma süresi

    @Column(length = 70)
    private String seoTitle; // Google aramaları için başlık

    @Column(length = 160)
    private String seoDescription; // Google aramaları için açıklama

    // -----------------------------

    private long viewCount = 0;
    private long likeCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ElementCollection
    @CollectionTable(name = "blog_tags", joinColumns = @JoinColumn(name = "blog_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}