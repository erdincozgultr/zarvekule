package com.zarvekule.gamification.entity;

import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "guilds")
@Data
@NoArgsConstructor
public class Guild {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @OneToOne
    @JoinColumn(name = "leader_id")
    private User leader;

    @ManyToMany
    @JoinTable(
            name = "guild_members",
            joinColumns = @JoinColumn(name = "guild_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    private int level = 1;

    private long xp = 0;

    private String bannerUrl;
    private String avatarUrl;
    private String discordWebhookUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}