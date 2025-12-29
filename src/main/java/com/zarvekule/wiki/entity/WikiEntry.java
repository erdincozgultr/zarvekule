package com.zarvekule.wiki.entity;

import com.zarvekule.user.entity.User;
import com.zarvekule.wiki.enums.ContentCategory;
import com.zarvekule.wiki.enums.WikiStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "wiki_entries", indexes = {
        @Index(name = "idx_wiki_slug", columnList = "slug"),
        @Index(name = "idx_wiki_category", columnList = "category"),
        @Index(name = "idx_wiki_status", columnList = "status"),
        @Index(name = "idx_wiki_source_key", columnList = "source_key")
})
@Data
public class WikiEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true, nullable = false, length = 255)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WikiStatus status = WikiStatus.PUBLISHED;

    /**
     * Orijinal API verisi - JSON olarak saklanır
     * Frontend kategori bazlı parse eder
     */
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    /**
     * Türkçe içerik - JSON olarak saklanır
     * Frontend kategori bazlı parse eder
     */
    @Type(JsonType.class)
    @Column(name = "turkish_content", columnDefinition = "jsonb")
    private Map<String, Object> turkishContent;

    /**
     * Duplicate kontrolü için kaynak anahtarı
     */
    @Column(name = "source_key", length = 255)
    private String sourceKey;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "like_count")
    private long likeCount = 0;

    @Column(name = "view_count")
    private long viewCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
