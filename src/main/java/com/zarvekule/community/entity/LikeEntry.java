package com.zarvekule.community.entity;

import com.zarvekule.community.enums.TargetType;
import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "like_entries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "target_type", "target_id"})
        })
@Data
@NoArgsConstructor
public class LikeEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    private LocalDateTime createdAt = LocalDateTime.now();
}