package com.zarvekule.gamification.dto;

import com.zarvekule.user.dto.UserSummaryDto;
import lombok.Data;

@Data
public class LeaderboardEntryDto {
    private int rank;
    private UserSummaryDto user;
    private long value; // XP, like count, content count vb.
    private String rankTier; // Kullanıcının rütbesi (Köylü, Maceracı, vb.)
}