package com.zarvekule.gamification.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GuildDto {
    private Long id;
    private String name;
    private String description;
    private int level;
    private long xp;
    private int memberCount;
    private UserSummaryDto leader;
    private LocalDateTime createdAt;
    private boolean isMember; // Kullanıcı bu guild'in üyesi mi?
}