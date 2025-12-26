package com.zarvekule.gamification.entity;

import com.zarvekule.gamification.enums.BadgeCategory;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "badges")
@Data
@NoArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String iconUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BadgeCategory category = BadgeCategory.OTHER;

    private String conditionCode;
}