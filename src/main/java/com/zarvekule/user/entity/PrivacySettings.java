package com.zarvekule.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "privacy_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrivacySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility = ProfileVisibility.PUBLIC;

    @Column(nullable = false)
    private boolean showActivity = true;

    @Column(nullable = false)
    private boolean showBadges = true;

    @Column(nullable = false)
    private boolean showGuild = true;

    @Column(nullable = false)
    private boolean showStats = true;

    public enum ProfileVisibility {
        PUBLIC,   // Herkese açık
        PRIVATE   // Sadece kendisi görebilir
    }
}