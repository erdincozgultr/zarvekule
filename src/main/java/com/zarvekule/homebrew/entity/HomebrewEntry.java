package com.zarvekule.homebrew.entity;

import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.user.entity.User;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "homebrew_entries")
@Data
public class HomebrewEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @Column(length = 1000)
    private String description; // Kısa açıklama (liste görünümü için)

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HomebrewCategory category;

    @Enumerated(EnumType.STRING)
    private HomebrewStatus status = HomebrewStatus.PENDING_APPROVAL;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content;

    @ElementCollection
    @CollectionTable(name = "homebrew_entry_tags", joinColumns = @JoinColumn(name = "entry_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "like_count")
    private long likeCount = 0;

    @Column(name = "view_count")
    private long viewCount = 0;

    // Fork sistemi için (opsiyonel)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_entry_id")
    private HomebrewEntry parentEntry;

    @Column(name = "fork_count")
    private long forkCount = 0;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}