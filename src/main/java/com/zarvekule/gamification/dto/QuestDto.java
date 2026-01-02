package com.zarvekule.gamification.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class QuestDto {
    private Long id;
    private String title;
    private String description;
    private String type;
    private int targetValue;
    private int currentValue;
    private int xpReward;
    private boolean completed;
    private LocalDateTime deadline;
    private int progressPercentage;
}