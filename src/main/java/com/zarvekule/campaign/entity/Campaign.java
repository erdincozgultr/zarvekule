package com.zarvekule.campaign.entity;

import com.zarvekule.campaign.enums.CampaignStatus;
import com.zarvekule.campaign.enums.GameFrequency;
import com.zarvekule.campaign.enums.GameSystem;
import com.zarvekule.campaign.enums.PlayPlatform;
import com.zarvekule.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, columnDefinition = "varchar(50)")
    private GameSystem system;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20)")
    private PlayPlatform platform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20)")
    private GameFrequency frequency;

    private String city;
    private String district;
    private String virtualTableLink;

    @Column(nullable = false)
    private int maxPlayers;

    @Column(nullable = false)
    private int currentPlayers = 0;

    private String levelRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20)")
    private CampaignStatus status = CampaignStatus.OPEN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dm_id", nullable = false)
    private User dungeonMaster;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;
}