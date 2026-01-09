package com.zarvekule.homebrew.entity;

import com.zarvekule.user.entity.User;
import com.zarvekule.wiki.entity.WikiEntry;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "homebrew_comments", indexes = {
        @Index(name = "idx_homebrew_comment_homebrew", columnList = "homebrew_id"),
        @Index(name = "idx_homebrew_comment_approved", columnList = "is_approved")
})
@Data
public class HomebrewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Homebrew'lar da WikiEntry olarak saklanıyor
     * Bu yüzden WikiEntry'ye referans veriyoruz
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homebrew_id", nullable = false)
    private WikiEntry homebrew;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}