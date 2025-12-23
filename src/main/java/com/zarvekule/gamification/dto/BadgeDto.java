package com.zarvekule.gamification.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BadgeDto {
    private Long id;
    private String name;
    private String description;
    private String iconUrl;
    private String conditionCode;
    private boolean earned; // Kullanıcı bu badge'i kazandı mı?
    private LocalDateTime earnedAt; // Kazanma tarihi (varsa)
}