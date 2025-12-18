package com.zarvekule.homebrew.entity;

import com.zarvekule.homebrew.enums.HomebrewCategory;
import com.zarvekule.homebrew.enums.HomebrewStatus;
import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
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
    private String description; // Kısa açıklama

    @Column(columnDefinition = "TEXT")
    private String excerpt; // Detaylı içerik veya alıntı

    private String imageUrl; // Resim Alanı

    @Enumerated(EnumType.STRING)
    private HomebrewCategory category;

    private String rarity;
    private String requiredLevel;

    @ElementCollection
    private Set<String> tags = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private HomebrewStatus status = HomebrewStatus.PENDING_APPROVAL;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    // Like sistemi için sayaç
    @Column(name = "like_count")
    private long likeCount = 0;

    @Column(name = "view_count")
    private long viewCount = 0;

    @ManyToOne
    @JoinColumn(name = "parent_entry_id")
    private HomebrewEntry parentEntry; // Fork sistemi için

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}