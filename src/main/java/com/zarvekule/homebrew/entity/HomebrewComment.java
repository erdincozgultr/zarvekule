package com.zarvekule.homebrew.entity;

import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "homebrew_comments")
@Data
public class HomebrewComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "homebrew_id", nullable = false)
    private HomebrewEntry homebrew;

    @Column(nullable = false)
    private Boolean isApproved = true; // Default true

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}