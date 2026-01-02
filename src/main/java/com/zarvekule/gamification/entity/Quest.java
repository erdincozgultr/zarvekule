// Quest.java
package com.zarvekule.gamification.entity;

import com.zarvekule.gamification.enums.QuestType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "guild_quests")
@Data
@NoArgsConstructor
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "guild_id", nullable = false)
    private Guild guild;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestType type;  // WEEKLY, MONTHLY

    @Column(nullable = false)
    private int targetValue;  // Hedef (örn: 10 homebrew)

    @Column(nullable = false)
    private int currentValue = 0;  // Şu anki ilerleme

    @Column(nullable = false)
    private int xpReward;  // Tamamlama ödülü

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDateTime completedAt;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}