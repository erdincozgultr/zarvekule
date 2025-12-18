package com.zarvekule.gamification.entity;

import com.zarvekule.gamification.enums.RankTier;
import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_gamification_stats")
@Data
@NoArgsConstructor
public class UserStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private long currentXp = 0;

    private int totalBlogs = 0;

    @Enumerated(EnumType.STRING)
    private RankTier currentRank = RankTier.PEASANT;

    private int totalComments = 0;
    private int totalHomebrews = 0;
    private int totalLikesReceived = 0;
}